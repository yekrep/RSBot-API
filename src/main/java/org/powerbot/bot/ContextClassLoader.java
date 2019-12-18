package org.powerbot.bot;

import org.powerbot.script.*;

@Script.Manifest(name = "RSBot", description = "powerbot.org", version = "8.0.2")
public abstract class ContextClassLoader extends ClassLoader {
	private final ClientContext ctx;

	public ContextClassLoader(final ClientContext ctx, final ClassLoader parent) {
		super(parent);
		this.ctx = ctx;
	}

	public ClientContext ctx() {
		return ctx;
	}
}
