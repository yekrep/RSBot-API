package org.powerbot.bot.rt6;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.ReflectorSpec;
import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;

public final class Bot extends AbstractBot<ClientContext> {
	public static final CacheWorker CACHE_WORKER = new CacheWorker(false);

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	protected void reflect(final ReflectorSpec s) {
		final Class c = chrome.target.get().getClass();
		final Queue<Field> q = new LinkedList<Field>();
		for (final Field f : c.getDeclaredFields()) {
			if (f.getType().getName().equals(Object.class.getName())) {
				q.offer(f);
			}
		}
		if (q.size() != 1) {
			System.out.println("Failed to identify client object.");
			return;
		}
		final Field f = q.poll();
		f.setAccessible(true);
		Object o = null;
		for (int i = 0; o == null && i < 10; ++i) {
			try {
				o = f.get((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC ? null : chrome.target.get());
			} catch (final IllegalAccessException ignored) {
			}
			try {
				Thread.sleep(500);
			} catch (final InterruptedException ignored) {
			}
		}
		if (o == null) {
			System.out.println("Failed to get client object.");
			return;
		}
		final Reflector r = new Reflector(o.getClass().getClassLoader(), s);
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
