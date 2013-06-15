package org.powerbot.script.internal;

import org.powerbot.script.Manifest;
import org.powerbot.script.internal.randoms.PollingPassive;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;

import java.util.logging.Logger;

public class RandomHandler implements Runnable {
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());
	private final ScriptContainer handler;
	private final PollingPassive[] events;
	private final Timer timeout;
	private PollingPassive active;
	private boolean suspended;

	public RandomHandler(final ScriptContainer handler, final PollingPassive[] events) {
		this.handler = handler;
		this.events = events;
		this.timeout = new Timer(0);
		this.suspended = false;
	}

	@Override
	public void run() {
		log.info("Random handler starting");
		while (!handler.isStopping()) {
			if (active != null) {
				//keep script suspended
				if (!handler.isSuspended()) handler.suspend();

				final String name = name(active);
				if (!active.isStopping()) {
					if (!timeout.isRunning()) {
						log.info("Random event failed: " + (name != null ? name : "unknown"));
						handler.stop();
						break;
					}
					Delay.sleep(600);
					continue;
				}
				log.info("Stopping random event: " + (name != null ? name : "unknown"));
				active = null;
				if (!suspended) handler.resume();
				continue;
			}
			try {
				for (final PollingPassive event : events) {
					if (event.isValid()) {
						active = event;
						break;
					}
				}
			} catch (final Exception ignored) {
			}
			if (active != null) {
				suspended = handler.isSuspended();
				final String name = name(active);
				log.info("Starting random event: " + (name != null ? name : "unknown"));
				timeout.setEndIn(Random.nextInt(600, 720) * 1000);
				handler.getExecutor().submit(active);
				if (!suspended) handler.suspend();
			} else {
				Delay.sleep(600);
			}
		}
		log.info("Random handler stopping");
		active = null;
	}

	private String name(final PollingPassive event) {
		final Class<?> c = event.getClass();
		if (c.isAnnotationPresent(Manifest.class)) {
			final Manifest randomManifest = c.getAnnotation(Manifest.class);
			final String name = randomManifest.name();
			if (!name.isEmpty()) return name;
		}
		return null;
	}
}