package org.powerbot.script;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.powerbot.script.util.Prioritizable;
import org.powerbot.script.util.ScriptController;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.util.Configuration;

/**
 * An abstract implementation of {@code Script}.
 *
 * @author Paris
 */
public abstract class AbstractScript implements Script, Prioritizable {
	private final Map<State, Collection<FutureTask<Boolean>>> tasks;
	private ScriptController controller;
	private final AtomicLong started, suspended;
	private final Queue<Long> suspensions;
	private final File dir;

	/**
	 * The designated {@link java.util.logging.Logger}, which should be used over {@code System.out.println}.
	 */
	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * The settings for this {@link org.powerbot.script.Script}, which are saved between runs.
	 */
	protected final Properties settings;

	public AbstractScript() {
		tasks = new ConcurrentHashMap<State, Collection<FutureTask<Boolean>>>(State.values().length);

		for (final State state : State.values()) {
			tasks.put(state, new ArrayDeque<FutureTask<Boolean>>());
		}

		tasks.get(State.START).add(new FutureTask<Boolean>(this, true));

		started = new AtomicLong(System.nanoTime());
		suspended = new AtomicLong(0);
		suspensions = new SynchronousQueue<Long>();

		tasks.get(State.START).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				started.set(System.nanoTime());
			}
		}, true));

		tasks.get(State.SUSPEND).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				suspensions.offer(System.nanoTime());
			}
		}, true));

		tasks.get(State.RESUME).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				suspended.addAndGet(System.nanoTime() - suspensions.poll());
			}
		}, true));

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

		getTasks(State.STOP).add(new FutureTask<Boolean>(new Runnable() {
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
			}
		}, true));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Collection<FutureTask<Boolean>> getTasks(final State state) {
		return tasks.get(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPriority(final int priority) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ScriptController getScriptController() {
		return controller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setScriptController(final ScriptController controller) {
		this.controller = controller;
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
	 * Returns the {@link org.powerbot.script.Manifest} attached to this {@link org.powerbot.script.Script} if present.
	 *
	 * @return the attached {@link org.powerbot.script.Manifest} if it exists, or {@code null} otherwise
	 */
	public Manifest getManifest() {
		return getClass().isAnnotationPresent(Manifest.class) ? getClass().getAnnotation(Manifest.class) : null;
	}

	/**
	 * Returns the name of this {@link org.powerbot.script.Script} as determined by its {@link org.powerbot.script.Manifest}.
	 *
	 * @return the name of this {@link org.powerbot.script.Script}
	 */
	public String getName() {
		final Manifest manifest = getManifest();
		return manifest == null || manifest.name() == null ? "" : manifest.name();
	}

	/**
	 * Returns the version of this {@link org.powerbot.script.Script} as determined by its {@link org.powerbot.script.Manifest}.
	 *
	 * @return the version of this {@link org.powerbot.script.Script}
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
