package org.powerbot.script;

import java.io.Closeable;
import java.util.logging.Logger;

import org.powerbot.gui.BotChrome;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = BotChrome.log;
	public final C ctx;
	protected final BotChrome chrome;

	public Bot(final BotChrome chrome) {
		this.chrome = chrome;
		ctx = newContext();
	}

	protected abstract C newContext();
}
