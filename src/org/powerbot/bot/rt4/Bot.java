package org.powerbot.bot.rt4;

import org.powerbot.bot.rt4.activation.EventDispatcher;
import org.powerbot.gui.BotLauncher;
import org.powerbot.script.rt4.ClientContext;

public class Bot extends org.powerbot.script.Bot<ClientContext> {
	public Bot(final BotLauncher launcher) {
		super(launcher, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	public void run() {
	}
}
