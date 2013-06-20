package org.powerbot.script.internal;

import org.powerbot.script.Script;
import org.powerbot.script.lang.Prioritizable;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;

public class PriorityManager implements Runnable, Prioritizable {
	private final ScriptController controller;
	private int priority;

	public PriorityManager(ScriptController controller) {
		this.controller = controller;
		this.priority = 0;
	}

	@Override
	public void run() {
		final int delay = 600;
		while (!controller.isStopping()) {
			int priority = 0;
			try {
				for (Script script : controller.getScripts()) {
					int p = script.getPriority();
					if (p > priority && script.isValid()) priority = p;
				}
			} catch (Throwable ignored) {
			}
			System.out.println(priority);
			this.priority = priority;

			Delay.sleep(Random.nextInt(delay / 2, delay * 2));
		}
	}

	@Override
	public int getPriority() {
		return priority;
	}
}
