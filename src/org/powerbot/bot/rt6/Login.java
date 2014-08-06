package org.powerbot.bot.rt6;

import java.awt.Rectangle;
import java.util.List;

import org.powerbot.misc.GameAccounts;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.Lobby;

public class Login extends PollingScript<ClientContext> {
	public static final String LOGIN_USER_PROPERTY = "login.account.username";

	public Login() {
		priority.set(4);
	}

	private boolean isValid() {
		if (ctx.properties.getProperty("login.disable", "").equals("true")) {
			return false;
		}

		final int state = ctx.game.clientState();
		return state == -1 || state == Constants.GAME_LOGIN ||
				state == Constants.GAME_LOBBY ||
				state == Constants.GAME_LOGGING;
	}

	@Override
	public void poll() {
		if (!isValid()) {
			if (threshold.contains(this)) {
				threshold.remove(this);
			}
			return;
		}
		if (!threshold.contains(this)) {
			threshold.add(this);
		}

		final GameAccounts.Account account = GameAccounts.getInstance().get(ctx.properties.getProperty(LOGIN_USER_PROPERTY, ""));
		final int state = ctx.game.clientState();

		if (state == Constants.GAME_LOBBY) {
			int world = -1;
			final String w = ctx.properties.getProperty("login.world", "-1");
			try {
				world = Integer.parseInt(w);
			} catch (final NumberFormatException ignored) {
			}

			if (world >= 0) {
				final Lobby.World current = ctx.lobby.world();
				final Lobby.World desired = ctx.lobby.world(world);
				if (current.number() != -1 && !current.equals(desired)) {
					if (!ctx.lobby.world(desired) && account != null) {
						final List<Lobby.World> worlds = ctx.lobby.worlds(new Filter<Lobby.World>() {
							@Override
							public boolean accept(final Lobby.World world) {
								final String str = account.member ? "Members" : "Free";
								return world.type().equalsIgnoreCase(str);
							}
						});
						if (worlds.size() > 0) {
							ctx.properties.put("login.world", Integer.toString(worlds.get(Random.nextInt(0, worlds.size())).number()));
						}
					}

					return;
				}
			}

			ctx.lobby.enterGame();
			return;
		}

		if (account != null && (state == Constants.GAME_LOGIN || state == Constants.GAME_LOGGING)) {
			final Component error = ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_ERROR);
			if (error.visible()) {
				final String txt = error.text().toLowerCase();

				if (txt.contains("your ban will be lifted in") || txt.contains("account has been disabled") || txt.contains("password") || txt.contains("ended")) {
					ctx.controller.stop();
					return;
				}

				ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_RETRY).click();
				return;
			}

			final String username = account.toString();
			final String password = account.getPassword();
			String text;
			text = getUsernameText();
			if (!text.equalsIgnoreCase(username)) {
				if (!clickLoginInterface(ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_USERNAME))) {
					return;
				}

				final int length = text.length();
				if (length > 0) {
					final StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.input.send(b.toString());
					return;
				}

				ctx.input.send(username);
				return;
			}

			text = getPasswordText();
			if (text.length() != password.length()) {
				if (!clickLoginInterface(ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_PASSWORD))) {
					return;
				}
				final int length = text.length();
				if (length > 0) {
					final StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.input.send(b.toString());
					return;
				}
				ctx.input.send(password);
				return;
			}

			ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_DO).click();
		}
	}

	private boolean clickLoginInterface(final Component i) {
		if (!i.valid()) {
			return false;
		}
		final Rectangle pos = i.boundingRect();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		final int midx = (int) pos.getCenterX();
		final int h = (int) pos.getHeight();
		final int midy = (int) (pos.getMinY() + (h == 0 ? 27 : h) / 2);
		if (i.index() == Constants.LOGIN_PASSWORD) {
			return ctx.input.click(getPasswordX(i), midy + Random.nextInt(-dy, dy), true);
		}
		return ctx.input.click(midx + Random.nextInt(1, maxRandomX), midy + Random.nextInt(-dy, dy), true);
	}

	private int getPasswordX(final Component a) {
		int x = 0;
		final Rectangle pos = a.boundingRect();
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return 0;
		}
		for (int i = 0; i < ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_PASSWORD).text().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + Random.nextInt(-dx, dx);
		}
	}

	private String getUsernameText() {
		return ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_USERNAME).text().toLowerCase();
	}

	public String getPasswordText() {
		return ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_PASSWORD).text();
	}
}
