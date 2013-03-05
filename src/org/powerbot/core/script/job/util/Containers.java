package org.powerbot.core.script.job.util;

import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.JobListener;

/**
 * A utility designed to aid in the manipulation of {@link Container}s.
 *
 * @author Timer
 */
@Deprecated
public class Containers {
	/**
	 * Waits for a container to reach a state of termination.
	 *
	 * @param container The {@link Container} to await to be terminated.
	 * @return <tt>true</tt> if the container was terminated; otherwise, <tt>false</tt>.
	 */
	public static boolean awaitTermination(final Container container) {
		return Containers.awaitTermination(container, 0);
	}

	/**
	 * Waits for a container to reach a state of termination.
	 *
	 * @param container The {@link Container} to await to be terminated.
	 * @param timeout   The maximum amount of time to wait for the {@link Container} to terminate.
	 * @return <tt>true</tt> if the container was terminated; otherwise, <tt>false</tt>.
	 */
	public static boolean awaitTermination(final Container container, final int timeout) {
		if (container.isTerminated()) {
			return true;
		}

		final Object lock = new Object();
		final JobListener listener = new JobListener() {
			@Override
			public void jobStarted(final Job job) {
			}

			@Override
			public void jobStopped(final Job job) {
				synchronized (lock) {
					lock.notify();
				}
			}
		};

		container.addListener(listener);
		final long mark_timeout = System.currentTimeMillis() + timeout;
		while ((timeout == 0 || System.currentTimeMillis() < mark_timeout) && !container.isTerminated()) {
			long left;
			if (timeout == 0) {
				left = 500;
			} else {
				final long now = System.currentTimeMillis();
				left = mark_timeout - now;
				if (left <= 0) {
					break;
				}
			}

			synchronized (lock) {
				try {
					lock.wait(left);
				} catch (final InterruptedException ignored) {
					container.removeListener(listener);
					throw new ThreadDeath();
				}
			}
		}
		container.removeListener(listener);

		return container.isTerminated();
	}
}
