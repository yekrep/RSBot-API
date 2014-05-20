package org.powerbot.bot;

public class ContextAccessor {
	public final Reflector engine;
	public final Object root;

	public ContextAccessor(final Reflector engine, final Object root) {
		this.engine = engine;
		this.root = root;
	}
}
