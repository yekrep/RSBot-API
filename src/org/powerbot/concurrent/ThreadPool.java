package org.powerbot.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory to maintain scripts within their bot (for context) and security.
 *
 * @author Timer
 */
public class ThreadPool implements ThreadFactory {
	public static final String THREADGROUPNAMEPREFIX = "ThreadPool@";
	public static final Map<Runnable, String> suffix = new HashMap<Runnable, String>();

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
		String addition = suffix.get(r);
		if (addition == null) {
			addition = "";

			final String threadName = Thread.currentThread().getName();
			if (threadName.contains(" /NAME/ ")) {
				final String[] threadData = threadName.split(" /NAME/ ");
				if (threadData.length == 2) {
					addition = threadData[1];
				}
			}
		}
		final StringBuilder builder = new StringBuilder(THREADGROUPNAMEPREFIX).append(hashCode()).append("-").append(threadNumber.getAndIncrement()).append('/').
				append(Thread.currentThread().getName()).append("@").append(Thread.currentThread().getThreadGroup());
		if (!addition.isEmpty()) {
			builder.append(" /NAME/ ");
			builder.append(addition);
		}
		return new Thread(threadGroup, r, builder.toString());
	}
}