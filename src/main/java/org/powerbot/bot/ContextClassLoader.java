package org.powerbot.bot;

import org.powerbot.script.*;

@Script.Manifest(name = "RSBot", description = "powerbot.org")
public abstract class ContextClassLoader extends ClassLoader {
	public static final int VERSION = 7102;

	public abstract ClientContext ctx();

	public abstract Input getInput(final Bot b);
}
