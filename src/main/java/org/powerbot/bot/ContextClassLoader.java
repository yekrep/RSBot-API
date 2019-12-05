package org.powerbot.bot;

import org.powerbot.script.*;

@Script.Manifest(name = "RSBot", description = "powerbot.org")
public abstract class ContextClassLoader extends ClassLoader {
	public static final int VERSION = 7118;

	public abstract ClientContext ctx();
}
