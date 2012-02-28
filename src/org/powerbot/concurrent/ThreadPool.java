package org.powerbot.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory to maintain scripts within their bot (for context) and security.
 *
 * @author Timer
 */
public class ThreadPool implements ThreadFactory {
	private AtomicInteger threadNumber;
	private ThreadGroup threadGroup;

	public ThreadPool(ThreadGroup threadGroup) {
		this.threadNumber = new AtomicInteger(1);
		this.threadGroup = threadGroup;
	}

	public Thread newThread(Runnable r) {
		return new Thread(threadGroup, r, "TaskPool-" + threadNumber.getAndIncrement());
	}
}