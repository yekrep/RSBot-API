package org.powerbot.script;

import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Random;
import org.powerbot.util.Configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public abstract class AbstractScript implements Script {
	public final Logger log = Logger.getLogger(getClass().getName());
	protected ClientFactory ctx;
	private ScriptContainer container;
	private final Map<Event, Deque<Callable<Boolean>>> triggers;
	private final AtomicLong started, suspended;
	private final Queue<Long> suspensions;
	private final File dir;
	protected final Properties settings;

	public AbstractScript() {
		this.triggers = new ConcurrentHashMap<>();
		for (Event event : Event.values()) {
			this.triggers.put(event, new ConcurrentLinkedDeque<Callable<Boolean>>());
		}

		started = new AtomicLong(System.nanoTime());
		suspended = new AtomicLong(0);
		suspensions = new ConcurrentLinkedQueue<>();

		triggers.get(Event.START).add(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				started.set(System.nanoTime());
				return true;
			}
		});

		triggers.get(Event.SUSPEND).add(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				suspensions.offer(System.nanoTime());
				return true;
			}
		});

		triggers.get(Event.RESUME).add(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				suspended.addAndGet(System.nanoTime() - suspensions.poll());
				return true;
			}
		});

		dir = new File(new File(System.getProperty("java.io.tmpdir"), Configuration.NAME), getClass().getName());
		final File xml = new File(dir, "settings.xml");
		settings = new Properties();

		if (xml.isFile() && xml.canRead()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(xml);
				settings.loadFromXML(in);
			} catch (final IOException ignored) {
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException ignored) {
					}
				}
			}
		}

		triggers.get(Event.STOP).add(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (settings.isEmpty()) {
					if (xml.isFile()) {
						xml.delete();
					}
				} else {
					if (!dir.isDirectory()) {
						dir.mkdirs();
					}
					BufferedOutputStream out = null;
					try {
						out = new BufferedOutputStream(new FileOutputStream(xml));
						settings.storeToXML(out, "");
					} catch (final IOException ignored) {
					} finally {
						if (out != null) {
							try {
								out.close();
							} catch (final IOException ignored) {
							}
						}
					}
				}
				return true;
			}
		});
	}

	@Override
	public final Deque<Callable<Boolean>> getTriggers(Event event) {
		return triggers.get(event);
	}

	@Override
	public final void setContainer(ScriptContainer container) {
		this.container = container;
	}

	@Override
	public final ScriptContainer getContainer() {
		return this.container;
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.ctx = clientFactory;
	}

	@Override
	public ClientFactory getClientFactory() {
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
		} catch (final InterruptedException e) {
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
}
