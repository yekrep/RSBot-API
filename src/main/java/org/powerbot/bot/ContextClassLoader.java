package org.powerbot.bot;

import org.powerbot.script.*;

@Script.Manifest(name = "RSBot", description = "powerbot.org")
public abstract class ContextClassLoader extends ClassLoader {
	public static final int VERSION = 7120;
	private final ClientContext ctx;

	public ContextClassLoader(final ClientContext ctx, final ClassLoader parent) {
		super(parent);
		this.ctx = ctx;
	}

	public ClientContext ctx() {
		return ctx;
	}
}
