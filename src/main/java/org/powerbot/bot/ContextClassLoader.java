package org.powerbot.bot;

import org.powerbot.script.ClientContext;

public abstract class ContextClassLoader extends ClassLoader {

	public abstract ClientContext ctx();
}
