package org.powerbot.script.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class BreakManager {
	private final AtomicBoolean breaking;
	private final Timer timer;

	public BreakManager() {
		this.breaking = new AtomicBoolean(false);
		this.timer = new Timer(0);
	}

	public boolean isBreaking() {
		return breaking.get() || timer.isRunning();
	}

	public void setBreaking(boolean breaking) {
		this.breaking.set(breaking);
		if (!breaking) {
			timer.reset();
		}
	}

	public Timer getTimer() {
		return timer;
	}
}
