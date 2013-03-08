package org.powerbot.script;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Timer;

/**
 * An implementation of {@code Script} which polls (or "loops") indefinitely.
 *
 * @author Paris
 */
public abstract class PollingScript extends AbstractScript implements Suspendable {
	private final Timer timer;
	private final AtomicBoolean suspended;

	public PollingScript() {
		suspended = new AtomicBoolean(false);

		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				timer.setDelay(poll());
			}
		});
		timer.setCoalesce(false);

		getTasks(State.SUSPEND).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				synchronized (timer) {
					suspended.set(true);
					timer.stop();
				}
			}
		}, true));

		getTasks(State.RESUME).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				synchronized (timer) {
					timer.start();
					suspended.set(false);
				}
			}
		}, true));

		getTasks(State.SUSPEND).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				suspend();
			}
		}, true));

		getTasks(State.RESUME).add(new FutureTask<Boolean>(new Runnable() {
			@Override
			public void run() {
				resume();
			}
		}, true));
	}

	/**
	 * The body of the loop.
	 *
	 * @return the delay in milliseconds before the next call
	 */
	public abstract int poll();

	/**
	 * Called when the script is first started.
	 */
	@Override
	public final void run() {
		timer.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSuspended() {
		return suspended.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void suspend() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void resume() {
	}
}
