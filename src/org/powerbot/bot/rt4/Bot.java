package org.powerbot.bot.rt4;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.ReflectorSpec;
import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.rt4.ClientContext;

public class Bot extends AbstractBot<ClientContext> {
	public static final CacheWorker CACHE_WORKER = new CacheWorker(true);

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	protected void reflect(final ReflectorSpec s) {
		final Reflector r = new Reflector(chrome.target.get().getClass().getClassLoader(), s);
		ctx.client(new Client(r, null));
		ctx.menu.register();
		ctx.chat.register();
	}
}
