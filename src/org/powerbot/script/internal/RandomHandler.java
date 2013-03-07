package org.powerbot.script.internal;

import java.util.logging.Logger;

import org.powerbot.script.internal.randoms.AntiRandom;
import org.powerbot.script.internal.randoms.RandomManifest;
import org.powerbot.script.task.Task;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;

public class RandomHandler extends Task {
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());
	private static final int ITERATION_DELAY = 1000;
	private final ScriptContainer handler;
	private final AntiRandom[] events;
	private final Timer timeout;
	private AntiRandom active;

	public RandomHandler(final ScriptContainer handler, final AntiRandom[] events) {
		this.handler = handler;
		this.events = events;
		this.timeout = new Timer(0);
	}

	@Override
	public void execute() {
		while (!handler.isStopped()) {
			if (active != null) {
				final String name = name(active);
				if (active.isActive()) {
					if (!timeout.isRunning()) {
						log.info("Random event failed: " + (name != null ? name : "unknown"));
						handler.stop();
						break;
					}
					sleep(ITERATION_DELAY);
					continue;
				}
				log.info("Stopping random event: " + (name != null ? name : "unknown"));
				active = null;
				continue;
			}
			try {
				for (final AntiRandom event : events) {
					if (event.valid()) {
						active = event;
						break;
					}
				}
			} catch (final Exception ignored) {
			}
			if (active != null) {
				final String name = name(active);
				log.info("Starting random event: " + (name != null ? name : "unknown"));
				timeout.setEndIn(Random.nextInt(600, 720) * 1000);
				getContainer().submit(active);
			} else sleep(ITERATION_DELAY);
		}

		if (active != null) active.interrupt();
	}

	private String name(final AntiRandom event) {
		final Class<?> c = event.getClass();
		if (c.isAnnotationPresent(RandomManifest.class)) {
			final RandomManifest randomManifest = c.getAnnotation(RandomManifest.class);
			final String name = randomManifest.name();
			if (!name.isEmpty()) return name;
		}
		return null;
	}
}
