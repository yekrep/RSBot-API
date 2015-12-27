package org.powerbot.script;

import java.util.logging.Logger;

public abstract class Bot<C extends ClientContext<? extends Client>> {
	protected final Logger log = Logger.getLogger("Bot");
	public final C ctx;

	public Bot() {
		ctx = newContext();
	}

	protected abstract C newContext();
}
