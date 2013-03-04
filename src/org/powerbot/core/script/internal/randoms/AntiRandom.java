package org.powerbot.core.script.internal.randoms;

import org.powerbot.core.script.job.LoopTask;

public abstract class AntiRandom extends LoopTask {
	public abstract boolean valid();

	public void onStart() {
	}

	public void onStop() {
	}
}
