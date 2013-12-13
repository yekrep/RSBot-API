package org.powerbot.script.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Paris
 */
public class ScriptThreadFactory implements ThreadFactory {
	public static final String NAME = "script";
	private static final AtomicInteger pool = new AtomicInteger(1);
	private final AtomicInteger thread = new AtomicInteger(1);
	protected final ThreadGroup group;
	protected final String prefix;

	public ScriptThreadFactory(final ThreadGroup group) {
		this.group = group;
		prefix =  "pool-" + pool.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(final Runnable r) {
		return new Thread(group, r, prefix + thread.getAndIncrement() + 0);
	}
}
