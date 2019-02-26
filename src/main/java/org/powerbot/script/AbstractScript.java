package org.powerbot.script;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import javax.imageio.ImageIO;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.util.*;

/**
 * AbstractScript
 * An abstract implementation of {@link Script}.
 *
 * @param <C> the type of client
 */
public abstract class AbstractScript<C extends ClientContext> implements Script {
	/**
	 * The {@link Logger} which should be used to print debugging messages.
	 */
	public final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * The {@link org.powerbot.script.ClientContext} for accessing client data.
	 */
	protected final C ctx;
	/**
	 * The user profile settings of this {@link AbstractScript}, which will be saved and reloaded between sessions.
	 */
	protected final Properties settings;

	private final List<Runnable>[] exec;
	private final File dir;

	/**
	 * Creates an instance of {@link AbstractScript}.
	 */
	@SuppressWarnings("unchecked")
	public AbstractScript() {
		@SuppressWarnings("unchecked")
		final List<Runnable>[] q = (List<Runnable>[]) new List[State.values().length];
		exec = q;
		for (int i = 0; i < exec.length; i++) {
			exec[i] = new CopyOnWriteArrayList<Runnable>();
		}

		final ClientContext x = ((ContextClassLoader) Thread.currentThread().getContextClassLoader()).ctx();
		final Class<?>[] o = {(Class<?>) x.bot().getScriptTypeArg(getClass()), null};
		o[1] = o[0] == null ? null : x.bot().getPrimaryClientContext(o[0]);
		if (o[0] != null && o[0] != o[1]) {
			try {
				final Constructor<?> ctor = o[0].getDeclaredConstructor(o[1]);
				final boolean a = ctor.isAccessible();
				ctor.setAccessible(true);
				@SuppressWarnings("unchecked")
				final C ctx = (C) ctor.newInstance(x);
				ctor.setAccessible(a);
				this.ctx = ctx;
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		} else {
			@SuppressWarnings("unchecked")
			final C ctx = (C) x;
			if (ctx == null) {
				throw new IllegalStateException("context unset");
			}
			this.ctx = ctx;
		}

		final String[] ids = {null, getName(), getClass().getName()};
		String id = "-";

		final ScriptBundle bundle = ctx.controller.bundle();
		if (bundle != null && bundle.definition != null) {
			ids[0] = bundle.definition.getID().replace('/', '-');
		}

		for (final String n : ids) {
			if (n != null && !n.isEmpty()) {
				id = n.replace("[^\\w\\s]", "_").trim();
				break;
			}
		}

		dir = new File(new File(Environment.TEMP, Environment.NAME), id);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		final File ini = new File(dir, "settings.1.ini");
		settings = new Properties();

		if (ini.isFile() && ini.canRead()) {
			settings.putAll(new Ini().read(ini).get().getMap());
		}

		exec[State.STOP.ordinal()].add(new Runnable() {
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

					final Map<String, String> map = new HashMap<String, String>(settings.size());
					synchronized (settings) {
						for (final Map.Entry<Object, Object> entry : settings.entrySet()) {
							map.put(entry.getKey().toString(), entry.getValue().toString());
						}
					}
					new Ini().put(map).write(ini);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Runnable> getExecQueue(final State state) {
		return exec[state.ordinal()];
	}

	/**
	 * Returns the total running time.
	 *
	 * @return the total runtime so far in milliseconds (including pauses)
	 */
	public long getTotalRuntime() {
		final AtomicLong[] times = ctx.controller.times();
		final long s = times[State.STOP.ordinal()].get();
		return TimeUnit.NANOSECONDS.toMillis((s == 0L ? System.nanoTime() : s) - times[State.START.ordinal()].get());
	}

	/**
	 * Returns the actual running time.
	 *
	 * @return the actual runtime so far in milliseconds
	 */
	public long getRuntime() {
		final AtomicLong[] times = ctx.controller.times();
		final long s = times[State.STOP.ordinal()].get();
		return TimeUnit.NANOSECONDS.toMillis((s == 0L ? System.nanoTime() : s) - times[State.START.ordinal()].get() - times[State.RESUME.ordinal()].get());
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
	 * Returns the {@link org.powerbot.script.Script.Manifest} attached to this {@link Script} if present.
	 *
	 * @return the attached {@link org.powerbot.script.Script.Manifest} if it exists, or {@code null} otherwise
	 */
	public Manifest getManifest() {
		return getClass().isAnnotationPresent(Manifest.class) ? getClass().getAnnotation(Manifest.class) : null;
	}

	/**
	 * Returns the name of this class as determined by its {@link org.powerbot.script.Script.Manifest}.
	 *
	 * @return the name
	 */
	public String getName() {
		final Manifest manifest = getManifest();
		return manifest == null || manifest.name() == null ? "" : manifest.name();
	}

	/**
	 * Returns the description of this class as determined by its {@link org.powerbot.script.Script.Manifest}.
	 *
	 * @return the description
	 */
	public String getDescription() {
		final Manifest m = getManifest();
		return m == null || m.description() == null ? "" : m.description();
	}

	/**
	 * Returns the properties of this class as determined by its {@link org.powerbot.script.Script.Manifest}.
	 *
	 * @return the key/value pair of properties
	 */
	public Map<String, String> getProperties() {
		final Manifest m = getManifest();
		final String s;
		if (m == null || (s = m.properties()) == null) {
			return Collections.emptyMap();
		}
		return ScriptBundle.parseProperties(s);
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

		final File p = f.getParentFile();
		if (p != null) {
			p.mkdirs();
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
			HttpUtils.download(u, f);
		} catch (final IOException ignored) {
		} catch (final SecurityException ignored) {
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
		final String name = "http/" + Integer.toHexString(url.hashCode());
		download(url, name);
		FileInputStream in = null;
		try {
			in = new FileInputStream(getFile(name));
			return IOUtils.readString(in);
		} catch (final IOException ignored) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
				}
			}
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
		c.update(StringUtils.getBytesUtf8(url));
		final File f = download(url, "images/" + Long.toHexString(c.getValue()));
		try {
			return ImageIO.read(f);
		} catch (final IOException ignored) {
			f.delete();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}

	public void openURL(final String url) {
		final String host;
		try {
			host = "." + new URL(url).getHost();
		} catch (final MalformedURLException ignored) {
			return;
		}

		final List<String> whitelist = new ArrayList<String>();
		whitelist.addAll(Arrays.asList(Environment.DOMAINS));

		for (final String w : whitelist) {
			if (host.endsWith("." + w)) {
				ctx.bot().openURL(url);
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String name = getName();
		return name == null || name.isEmpty() ? getClass().getSimpleName() : name;
	}
}
