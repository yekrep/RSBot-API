package org.powerbot.bot.rt6;

import java.util.Hashtable;
import java.util.TimerTask;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.ReflectorSpec;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;

public final class Bot extends AbstractBot<ClientContext> {
	private Hashtable<String, Class<?>> loaded;

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	protected void reflect(final ReflectorSpec s) {
		final Reflector r = new Reflector(loaded.get("client").getClassLoader(), s);
		ctx.client(new Client(r, null));
		ctx.chat.register();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (ctx.game == null) {
					return;
				}
				final int s = ctx.game.clientState();
				if (s == Constants.GAME_LOGIN || s == Constants.GAME_LOGGING) {
					final org.powerbot.script.rt6.Component e = ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_ERROR);
					if (e.visible()) {
						String m = null;
						final String txt = e.text().toLowerCase();

						if (txt.contains(Login.ERROR_BAN)) {
							m = "ban";
						} else if (txt.contains(Login.ERROR_DISABLED)) {
							m = "disabled";
						} else if (txt.contains(Login.ERROR_RULEBREAKING)) {
							m = "rules";
						}

						if (m != null) {
							GoogleAnalytics.getInstance().pageview("scripts/0/login/" + m, txt);
						}
					}
				}
			}
		}, 6000, 3000);
	}
}
