package org.powerbot.script;

import java.lang.reflect.Type;
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

	public abstract void openURL(final String u);

	public abstract Type getScriptTypeArg(final Class<? extends AbstractScript> c);

	public abstract Class<?> getPrimaryClientContext(final Class<?> c);
}
