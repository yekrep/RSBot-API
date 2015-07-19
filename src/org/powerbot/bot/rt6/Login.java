package org.powerbot.bot.rt6;

import java.awt.Rectangle;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.misc.GameAccounts;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.Lobby;
import org.powerbot.util.StringUtils;

public class Login extends PollingScript<ClientContext> {
	public static final String LOGIN_USER_PROPERTY = "login.account.username";
	static final String ERROR_BAN = "your ban will be lifted in", ERROR_DISABLED = "account has been disabled", ERROR_RULEBREAKING = "serious rule breaking";
	private volatile String user, pass;

	public Login() {
		priority.set(4);
		user = "";
		pass = "";
	}

	private boolean isValid() {
		final Client c = ctx.client();
		if (c == null || ctx.properties.getProperty("login.disable", "").equals("true")) {
			return false;
		}
		final int state = ctx.game.clientState();
		if (ctx.properties.getProperty("lobby.disable", "").equals("true") &&
				state == Constants.GAME_LOBBY) {
			return false;
		}

		final String u = c.getCurrentUsername(), p = c.getCurrentPassword();
		if ((state == Constants.GAME_LOBBY || state == Constants.GAME_MAP_LOADED) && user.isEmpty() && !user.equals(u)) {
			user = u == null ? "" : u;
			pass = p == null ? "" : p;
		}

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

		final String username, password;
		final GameAccounts g = GameAccounts.getInstance();
		final GameAccounts.Account account;
		final GameAccounts.Account a = g.get(ctx.properties.getProperty(LOGIN_USER_PROPERTY, ""));
		if (a != null) {
			account = a;
			username = a.toString();
			password = a.getPassword();
		} else if (user.isEmpty() || pass.isEmpty()) {
			username = null;
			password = null;
			account = null;
		} else {
			ctx.properties.put(LOGIN_USER_PROPERTY, username = user);
			password = pass;
			account = g.contains(username) ? g.get(username) : null;
		}
		final int state = ctx.game.clientState();

		if (state == Constants.GAME_LOBBY && !ctx.properties.getProperty("lobby.disable", "").equals("true")) {
			final Component c2 = ctx.widgets.component(Constants.LOBBY_WIDGET, Constants.LOBBY_ERROR);
			if (c2.visible()) {
				c2.click();
			}
			int world = StringUtils.parseInt(ctx.properties.getProperty("login.world", "-1"));
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

		if (username != null && password != null && (state == Constants.GAME_LOGIN || state == Constants.GAME_LOGGING)) {
			final Component error = ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_ERROR);
			if (error.visible()) {
				final String txt = error.text().toLowerCase();

				if (txt.contains(ERROR_BAN) || txt.contains(ERROR_DISABLED) || txt.contains("password") || txt.contains("ended")) {
					ctx.controller.stop();
					return;
				}

				ctx.widgets.component(Constants.LOGIN_WIDGET, Constants.LOGIN_RETRY).click();
				return;
			}

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
