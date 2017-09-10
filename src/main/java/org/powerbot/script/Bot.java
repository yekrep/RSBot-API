package org.powerbot.script;

import java.util.logging.Logger;

/**
 * Bot
 * An intermediary object containing references to the client applet, endpoints, and event dispatchers.
 *
 * @param <C> the type of context
 */
public abstract class Bot<C extends ClientContext<? extends Client>> {
	public final C ctx;
	protected final Logger log = Logger.getLogger("Bot");

	public Bot() {
		ctx = newContext();
	}

	protected abstract C newContext();

	public abstract boolean allowTrades();
}
