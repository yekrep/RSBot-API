package org.powerbot.script;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import javax.imageio.ImageIO;

import org.powerbot.Configuration;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;

/**
 * An abstract implementation of {@link Script}.
 */
public abstract class AbstractScript implements Script {
	/**
	 * The {@link Logger} which should be used to print debugging messages.
	 */
	public final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * The {@link MethodContext} for accessing client data.
	 */
	protected final MethodContext ctx;

	private ScriptController controller;
	private final Map<State, Queue<Runnable>> exec;
	private final AtomicLong started, suspended;
	private final Queue<Long> suspensions;
	private final File dir;

	/**
	 * The user profile settings of this {@link AbstractScript}, which will be saved and reloaded between sessions.
	 */
	protected final Properties settings;

	/**
	 * Creates an instance of {@link AbstractScript}.
	 */
	public AbstractScript() {
		ctx = new MethodContext();
		exec = new ConcurrentHashMap<>(State.values().length);
		for (final State state : State.values()) {
			exec.put(state, new ConcurrentLinkedQueue<Runnable>());
		}

		started = new AtomicLong(System.nanoTime());
		suspended = new AtomicLong(0);
		suspensions = new ConcurrentLinkedQueue<>();

		exec.get(State.START).add(new Runnable() {
			@Override
			public void run() {
				started.set(System.nanoTime());
			}
		});

		exec.get(State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				suspensions.offer(System.nanoTime());
			}
		});

		exec.get(State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				suspended.addAndGet(System.nanoTime() - suspensions.poll());
			}
		});

		dir = new File(new File(Configuration.TEMP, Configuration.NAME), getClass().getName());
		final File ini = new File(dir, "settings.1.ini");
		settings = new Properties();

		if (ini.isFile() && ini.canRead()) {
			try {
				final Map<String, Map<String, String>> data = IniParser.deserialise(ini);
				if (data != null && data.containsKey(IniParser.EMPTYSECTION)) {
					for (final Map.Entry<String, String> entry : data.get(IniParser.EMPTYSECTION).entrySet()) {
						settings.put(entry.getKey(), entry.getValue());
					}
				}
			} catch (final IOException ignored) {
			}
		}

		exec.get(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				if (settings.isEmpty()) {
					if (ini.isFile()) {
						ini.delete();
					}
				} else {
					if (!dir.isDirectory()) {
						dir.mkdirs();
					}
					final Map<String, Map<String, String>> data = new HashMap<>(1);
					synchronized (settings) {
						final Map<String, String> map = new HashMap<>(settings.size());
						for (final Map.Entry<Object, Object> entry : settings.entrySet()) {
							map.put(entry.getKey().toString(), entry.getValue().toString());
						}
						data.put(IniParser.EMPTYSECTION, map);
					}
					try {
						IniParser.serialise(data, ini);
					} catch (final IOException ignored) {
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Queue<Runnable> getExecQueue(final State state) {
		return exec.get(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setController(final ScriptController controller) {
		this.controller = controller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ScriptController getController() {
		return controller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContext(final MethodContext ctx) {
		this.ctx.init(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodContext getContext() {
		return ctx;
	}

	/**
	 * Returns the total running time.
	 *
	 * @return the total runtime so far in milliseconds (including pauses)
	 */
	public long getTotalRuntime() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started.get());
	}

	/**
	 * Returns the actual running time.
	 *
	 * @return the actual runtime so far in milliseconds
	 */
	public long getRuntime() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started.get() - suspended.get());
	}

	/**
	 * Returns the designated storage folder.
	 *
	 * @return a directory path where files can be saved to and read from
	 */
	public File getStorageDirectory() {
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * Returns the {@link Manifest} attached to this {@link Script} if present.
	 *
	 * @return the attached {@link Manifest} if it exists, or {@code null} otherwise
	 */
	public Manifest getManifest() {
		return getClass().isAnnotationPresent(Manifest.class) ? getClass().getAnnotation(Manifest.class) : null;
	}

	/**
	 * Returns the name of this {@link Script} as determined by its {@link Manifest}.
	 *
	 * @return the name of this {@link Script}
	 */
	public String getName() {
		final Manifest manifest = getManifest();
		return manifest == null || manifest.name() == null ? "" : manifest.name();
	}

	/**
	 * Returns the version of this {@link Script} as determined by its {@link Manifest}.
	 *
	 * @return the version of this {@link Script}
	 */
	public double getVersion() {
		final Manifest manifest = getManifest();
		if (manifest == null) {
			try {
				return (double) Manifest.class.getMethod("version").getDefaultValue();
			} catch (final NoSuchMethodException ignored) {
				return 1d;
			}
		}
		return manifest.version();
	}

	/**
	 * Returns a {@link java.io.File} from an abstract local file name.
	 *
	 * @param name a local file name, which may contain path separators
	 * @return the fully qualified {@link java.io.File} inside the {@link #getStorageDirectory()}
	 */
	public File getFile(final String name) {
		File f = getStorageDirectory();

		for (final String part : name.split("\\|/")) {
			f = new File(f, part);
		}

		return f;
	}

	/**
	 * Downloads a file via HTTP/HTTPS. Server side caching is supported to reduce bandwidth.
	 *
	 * @param url  the HTTP/HTTPS address of the remote resource to download
	 * @param name a local file name, path separators are supported
	 * @return the {@link java.io.File} of the downloaded resource
	 */
	public File download(final String url, final String name) {
		final File f = getFile(name);

		final URL u;
		try {
			u = new URL(url);
		} catch (final MalformedURLException ignored) {
			return f;
		}

		try {
			HttpClient.download(u, f);
		} catch (final IOException ignored) {
			f.delete();
		}

		return f;
	}

	/**
	 * Reads a HTTP/HTTPS resource into a string.
	 *
	 * @param url the HTTP/HTTPS address of the remote resource to read
	 * @return a string representation of the downloaded resource
	 */
	public String downloadString(final String url) {
		final String name = "http-" + Integer.toHexString(url.hashCode());
		download(url, name);
		try (final FileInputStream in = new FileInputStream(getFile(name))) {
			return IOHelper.readString(in);
		} catch (final IOException ignored) {
		}
		return "";
	}

	/**
	 * Returns a downloaded image resource as a usable {@link java.awt.image.BufferedImage}.
	 *
	 * @param url the HTTP/HTTPS address of the remote image file
	 * @return a {@link java.awt.image.BufferedImage}, which will be a blank 1x1 pixel if the remote image failed to download
	 */
	public BufferedImage downloadImage(final String url) {
		final Adler32 c = new Adler32();
		c.update(StringUtil.getBytesUtf8(url));
		final File f = download(url, "images/" + Long.toHexString(c.getValue()));
		try {
			return ImageIO.read(f);
		} catch (final IOException ignored) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}
}
