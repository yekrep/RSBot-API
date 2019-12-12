package org.powerbot.bot;

import org.powerbot.script.*;

@Script.Manifest(name = "RSBot", description = "powerbot.org", version = "7121")
public abstract class ContextClassLoader extends ClassLoader {
	@Deprecated
	public static final int VERSION = 7121;
	private final ClientContext ctx;

	public ContextClassLoader(final ClientContext ctx, final ClassLoader parent) {
		super(parent);
		this.ctx = ctx;
	}

	public ClientContext ctx() {
		return ctx;
	}
}
