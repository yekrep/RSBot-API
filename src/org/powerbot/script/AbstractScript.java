package org.powerbot.script;

import org.powerbot.Configuration;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.zip.Adler32;

public abstract class AbstractScript implements Script {
	public final Logger log = Logger.getLogger(getClass().getName());
	protected MethodContext ctx;
	private ScriptController controller;
	private final Map<State, Queue<Runnable>> exec;
	private final AtomicLong started, suspended;
	private final Queue<Long> suspensions;
	private final File dir;
	protected final Properties settings;

	public AbstractScript() {
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
		final File xml = new File(dir, "settings.xml");
		settings = new Properties();

		if (xml.isFile() && xml.canRead()) {
			try (final FileInputStream in = new FileInputStream(xml)) {
				settings.loadFromXML(in);
			} catch (final IOException ignored) {
			}
		}

		exec.get(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				if (settings.isEmpty()) {
					if (xml.isFile()) {
						xml.delete();
					}
				} else {
					if (!dir.isDirectory()) {
						dir.mkdirs();
					}
					try (final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(xml))) {
						settings.storeToXML(out, "");
					} catch (final IOException ignored) {
					}
				}
			}
		});
	}

	@Override
	public final Queue<Runnable> getExecQueue(final State state) {
		return exec.get(state);
	}

	@Override
	public final void setController(final ScriptController group) {
		this.controller = group;
	}

	@Override
	public final ScriptController getController() {
		return controller;
	}

	@Override
	public void setContext(final MethodContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public MethodContext getContext() {
		return ctx;
	}

	/**
	 * Sleeps for the specified duration.
	 *
	 * @param millis the duration in milliseconds.
	 */
	public void sleep(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	/**
	 * Sleeps for a random duration between the specified intervals.
	 *
	 * @param min the minimum duration (inclusive)
	 * @param max the maximum duration (exclusive)
	 */
	public void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}

	/**
	 * Returns the total running time.
	 *
	 * @return the total runtime so far in seconds (including pauses)
	 */
	public long getTotalRuntime() {
		return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - started.get());
	}

	/**
	 * Returns the actual running time.
	 *
	 * @return the actual runtime so far in seconds
	 */
	public long getRuntime() {
		return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - started.get() - suspended.get());
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
	 * Returns the {@link org.powerbot.script.Manifest} attached to this {@link Script} if present.
	 *
	 * @return the attached {@link org.powerbot.script.Manifest} if it exists, or {@code null} otherwise
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
	 * Downloads a file via HTTP/HTTPS. Server side caching is supported to reduce bandwidth.
	 *
	 * @param url  the HTTP/HTTPS address of the remote resource to download
	 * @param name a local file name, path separators are supported
	 * @return the {@link java.io.File} of the downloaded resource
	 */
	public File download(final String url, final String name) {
		File f = getStorageDirectory();

		for (final String part : name.split("\\|/")) {
			f = new File(f, part);
		}

		final URL u;
		try {
			u = new URL(url);
		} catch (final MalformedURLException ignored) {
			return f;
		}

		try {
			HttpClient.download(u, f);
		} catch (final IOException ignored) {
		}

		return f;
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
