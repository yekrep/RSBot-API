package org.powerbot.script.task;

import java.util.logging.Logger;

import org.powerbot.script.util.Random;

public abstract class Task implements Runnable {
	public final Logger log = Logger.getLogger(getClass().getName());

	public static void sleep(final int delay) {
		Thread t = Thread.currentThread();
		if (t.isInterrupted()) throw new ThreadDeath();
		if (delay <= 0) return;
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ignored) {
			throw new ThreadDeath();
		}
	}

	public static void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}

	public abstract void execute();

	@Override
	public final void run() {
		try {
			execute();
		} catch (ThreadDeath ignored) {
			//dispose of thread deaths
			//jdk says we're supposed to rethrow this, except in this case
			//a death is handled and the thread is shortly terminated
			//following this point, not to mention we want to keep it cached
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		}
	}
}
