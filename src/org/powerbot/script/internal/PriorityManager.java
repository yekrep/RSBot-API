package org.powerbot.script.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.script.Script;
import org.powerbot.script.lang.Prioritizable;
import org.powerbot.script.util.Random;

public class PriorityManager implements Runnable, Prioritizable {
	private final ScriptController controller;
	private final AtomicBoolean running;
	private int priority;

	public PriorityManager(ScriptController controller) {
		this.controller = controller;
		this.running = new AtomicBoolean(false);
		this.priority = 0;
	}

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			return;
		}

		final int delay = 600;
		while (!controller.isStopping()) {
			int priority = 0;
			for (Script script : controller.scripts) {
				if (!(script instanceof YieldableTask)) {
					continue;
				}

				try {
					YieldableTask task = (YieldableTask) script;
					int p = task.getPriority();
					if (p > priority && task.isValid()) {
						priority = p;
					}
				} catch (Exception ignored) {
				}
			}
			this.priority = priority;

			try {
				Thread.sleep(Random.nextInt(delay / 2, delay * 2));
			} catch (InterruptedException ignored) {
			}
		}

		running.set(false);
	}

	@Override
	public int getPriority() {
		return priority;
	}
}