package org.powerbot.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory to maintain scripts within their bot (for context) and security.
 *
 * @author Timer
 */
public class ThreadPool implements ThreadFactory {
	private final AtomicInteger threadNumber;
	private final ThreadGroup threadGroup;

	public ThreadPool(final ThreadGroup threadGroup) {
		threadNumber = new AtomicInteger(1);
		this.threadGroup = threadGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public Thread newThread(final Runnable r) {
		return new Thread(
				threadGroup, r,
				new StringBuilder(ThreadPool.class.getName()).append('@').append(hashCode()).append("-").append(threadNumber.getAndIncrement()).append('/').
						append(Thread.currentThread().getName()).append("@").append(Thread.currentThread().getThreadGroup()).toString()
		);
	}
}