package org.powerbot.bot;

public class ContextAccessor {
	public final ReflectionEngine engine;
	protected final Object parent;

	public ContextAccessor(final ReflectionEngine engine, final Object parent) {
		this.engine = engine;
		this.parent = parent;
	}
}
