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
	private int pos;
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
			if (pos != -1) {
				//keep script suspended
				if (!handler.isSuspended()) handler.suspend();

				final String name = getName(events[pos]);
				if (!events[pos].isStopping()) {
					if (!timeout.isRunning()) {
						log.info("Random event failed: " + (name != null ? name : "unknown"));
						handler.stop();
						break;
					}
					int cursor = getCursor();
					if (cursor == -1 || cursor == pos) {
						Delay.sleep(600);
						continue;
					}
				}
				log.info("Stopping random event: " + (name != null ? name : "unknown"));
				pos = -1;
				if (!suspended) handler.resume();
				continue;
			}
			int cursor = getCursor();
			if (cursor != -1) {
				pos = cursor;
			}
			if (pos != -1) {
				suspended = handler.isSuspended();
				final String name = getName(events[pos]);
				log.info("Starting random event: " + (name != null ? name : "unknown"));
				timeout.setEndIn(Random.nextInt(600, 720) * 1000);
				handler.getExecutor().submit(events[pos]);
				if (!suspended) handler.suspend();
			} else {
				Delay.sleep(600);
			}
		}
		log.info("Random handler stopping");
		pos = -1;
	}

	private String getName(final PollingPassive event) {
		final Class<?> c = event.getClass();
		if (c.isAnnotationPresent(Manifest.class)) {
			final Manifest randomManifest = c.getAnnotation(Manifest.class);
			final String name = randomManifest.name();
			if (!name.isEmpty()) return name;
		}
		return null;
	}

	private int getCursor() {
		for (int i = 0; i < events.length; i++) {
			try {
				if (events[i].isValid()) return i;
			} catch (Exception ignored) {
			}
		}
		return -1;
	}
}