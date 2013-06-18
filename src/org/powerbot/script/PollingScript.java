package org.powerbot.script;

public abstract class PollingScript extends AbstractScript {

	public PollingScript() {
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				start();
			}
		});
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

	public abstract int poll();

	@Override
	public final void run() {
		final int delay = 600;

		while (!getController().isStopping()) {
			final int sleep;

			if (getController().isSuspended()) {
				sleep = delay;
			} else {
				try {
					sleep = poll();
				} catch (final Throwable t) {
					t.printStackTrace();
					getController().stop();
					break;
				}
			}

			sleep(Math.max(0, sleep == -1 ? delay : sleep));
		}
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for the specified number of milliseconds.
	 *
	 * @param millis the length of time to sleep in milliseconds
	 */
	public final void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	/**
	 * Called on {@link State#START}.
	 * This method can either be overriden or ignored.
	 */
	public void start() {
	}

	/**
	 * Called on {@link State#STOP}.
	 * This method can either be overriden or ignored.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link State#SUSPEND}.
	 * This method can either be overriden or ignored.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link State#RESUME}.
	 * This method can either be overriden or ignored.
	 */
	public void resume() {
	}
}
