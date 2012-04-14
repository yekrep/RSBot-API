package org.powerbot.game.bot;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Context {
	private static Logger log = Logger.getLogger(Context.class.getName());
	protected static final Map<ThreadGroup, Context> context = new HashMap<ThreadGroup, Context>();

	private final Bot bot;

	public Context(final Bot bot) {
		this.bot = bot;
	}

	public static Context get() {
		final Context context = Context.context.get(Thread.currentThread().getThreadGroup());
		if (context == null) {
			final RuntimeException exception = new RuntimeException(Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
			log.log(Level.SEVERE, "Client does not exist: ", exception);
			throw exception;
		}
		return context;
	}

	public static Bot resolve() {
		return get().bot;
	}

	public Bot bot() {
		return bot;
	}

	public void associate(final ThreadGroup threadGroup) {
		if (!EventQueue.isDispatchThread() && Context.context.containsKey(threadGroup)) {
			throw new RuntimeException("overlapping thread groups!");
		}
		Context.context.put(threadGroup, this);
	}

	public void disregard(final ThreadGroup threadGroup) {
		Context.context.remove(threadGroup);
	}
}
