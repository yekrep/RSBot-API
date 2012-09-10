package org.powerbot.concurrent;

import java.util.EventListener;

import org.powerbot.game.api.util.Time;

@Deprecated
public abstract class LoopTask implements Runnable, EventListener {
	protected boolean running;
	protected boolean killed;

	public LoopTask() {
		running = true;
		killed = false;
	}

	public abstract int loop();

	@Override
	public void run() {
		while (running) {
			final int wait = loop();
			if (wait > 0) {
				Time.sleep(wait);
			} else if (wait == -1) {
				running = false;
				kill();
			}
		}
	}

	public void start() {
		running = true;
	}

	public void stop() {
		running = false;
	}

	public void kill() {
		killed = true;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isKilled() {
		return killed;
	}
}
