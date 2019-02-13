package org.powerbot.bot;

import org.powerbot.script.Bot;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Input;

public abstract class ContextClassLoader extends ClassLoader {

	public abstract ClientContext ctx();

	public abstract Input getInput(final Bot b);
}
