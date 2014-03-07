package org.powerbot.script;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops") indefinitely.
 */
public abstract class PollingScript extends AbstractScript implements Runnable {

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				start();
			}
		});
		getExecQueue(State.START).add(this);
		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				stop();
			}
		});
		getExecQueue(State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				suspend();
			}
		});
		getExecQueue(State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				resume();
			}
		});
	}

	/**
	 * The main body of this {@link PollingScript}, which is called in a single-threaded loop.
	 */
	public abstract void poll();

	@Override
	public final void run() {
		try {
			poll();
			if (!Thread.interrupted()) {
				ctx.controller.offer(this);
			}
		} catch (final Throwable e) {
			ctx.controller.stop();
			e.printStackTrace();
		}

		Thread.yield();
	}

	/**
	 * Called on {@link org.powerbot.script.lang.Script.State#START}.
	 * This method can either be overridden or ignored.
	 */
	public void start() {
	}

	/**
	 * Called on {@link org.powerbot.script.lang.Script.State#STOP}.
	 * This method can either be overridden or ignored.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link org.powerbot.script.lang.Script.State#SUSPEND}.
	 * This method can either be overridden or ignored.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link org.powerbot.script.lang.Script.State#RESUME}.
	 * This method can either be overridden or ignored.
	 */
	public void resume() {
	}
}
