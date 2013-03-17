package org.powerbot.script.util;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A named thread group pool executor.
 *
 * @author Paris
 * @author Timer
 */
public class NamedCachedThreadPoolExecutor extends ThreadPoolExecutor {

	public NamedCachedThreadPoolExecutor() {
		super(0, Integer.MAX_VALUE, 48L, TimeUnit.HOURS, new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory());
		setThreadFactory(new GroupedThreadFactory(new ThreadGroup(getClass().getName() + "@" + Integer.toHexString(hashCode()))));
	}

	private final class GroupedThreadFactory implements ThreadFactory {
		private final String id;
		private final ThreadGroup threadGroup;
		private final AtomicInteger c;

		private GroupedThreadFactory(final ThreadGroup threadGroup) {
			this.id = getClass().getName() + "@" + Integer.toHexString(hashCode());
			this.threadGroup = threadGroup;
			this.c = new AtomicInteger(0);
		}

		@Override
		public Thread newThread(final Runnable r) {
			return new Thread(threadGroup, r, id + c.incrementAndGet());
		}
	}
}
