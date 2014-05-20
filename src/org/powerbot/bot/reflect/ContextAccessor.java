package org.powerbot.bot.reflect;

public class ContextAccessor {
	public final ReflectionEngine engine;
	public final Object root;

	public ContextAccessor(final ReflectionEngine engine, final Object root) {
		this.engine = engine;
		this.root = root;
	}
}
