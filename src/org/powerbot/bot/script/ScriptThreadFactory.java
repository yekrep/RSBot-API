package org.powerbot.bot.script;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Paris
 */
public class ScriptThreadFactory implements ThreadFactory {
	public static final String NAME = "script";
	private static final AtomicInteger pool = new AtomicInteger(1);
	private final AtomicInteger thread = new AtomicInteger(1);
	private final ThreadGroup group;
	private final String prefix;
	private final ClassLoader cl;

	public ScriptThreadFactory(final ThreadGroup group, final ClassLoader cl) {
		this.group = group;
		prefix = "pool-" + pool.getAndIncrement() + "-thread-";
		this.cl = cl;
	}

	@Override
	public Thread newThread(final Runnable r) {
		final Thread t = new Thread(group, r, prefix + thread.getAndIncrement() + 0);
		t.setContextClassLoader(cl);
		return t;
	}
}
