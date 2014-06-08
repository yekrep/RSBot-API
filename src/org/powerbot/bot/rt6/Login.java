package org.powerbot.bot.rt6;

import java.awt.Rectangle;

import org.powerbot.misc.GameAccounts;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Game;
import org.powerbot.script.rt6.Lobby;

public class Login extends PollingScript<ClientContext> {
	private static final int WIDGET = 596, WIDGET_VALIDATION = 906;
	private static final int WIDGET_LOGIN_ERROR = 57;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 84;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 90;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 93;
	private static final int WIDGET_LOGIN_BUTTON = 105;
	private static final int WIDGET_VALIDATE_CHANGE = 476;

	public static final String LOGIN_USER_PROPERTY = "login.account.username";

	public Login() {
		priority.set(4);
	}

	private boolean isValid() {
		if (ctx.properties.getProperty("login.disable", "").equals("true")) {
			return false;
		}

		final int state = ctx.game.clientState();
		return state == -1 || state == Game.INDEX_LOGIN_SCREEN ||
				state == Game.INDEX_LOBBY_SCREEN ||
				state == Game.INDEX_LOGGING_IN;
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

		if (state == Game.INDEX_LOBBY_SCREEN) {
			int world = -1;
			final String w = ctx.properties.getProperty("login.world", "-1");
			try {
				world = Integer.parseInt(w);
			} catch (final NumberFormatException ignored) {
			}

			if (ctx.widgets.component(WIDGET_VALIDATION, WIDGET_VALIDATE_CHANGE).visible()) {
				ctx.controller.stop();
			}

			if (world > 0) {
				final Lobby.World world_wrapper;
				if ((world_wrapper = ctx.lobby.world(world)) != null) {
					if (!ctx.lobby.enterGame(world_wrapper) && account != null) {
						final Lobby.World[] worlds = ctx.lobby.worlds(new Filter<Lobby.World>() {
							@Override
							public boolean accept(final Lobby.World world) {
								return world.members() == account.member;
							}
						});
						if (worlds.length > 0) {
							ctx.properties.put("login.world", Integer.toString(worlds[Random.nextInt(0, worlds.length)].number()));
						}
					}
					return;
				}
			}
			ctx.lobby.enterGame();
			return;
		}

		if (account != null && (state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN)) {
			final Component error = ctx.widgets.component(WIDGET, WIDGET_LOGIN_ERROR);
			if (error.visible()) {
				final String pre = "scripts/0/login/", txt = error.text().toLowerCase();
				boolean stop = false;

				if (txt.contains("your ban will be lifted in")) {
					GoogleAnalytics.getInstance().pageview(pre + "ban", txt);
					stop = true;
				} else if (txt.contains("account has been disabled")) {
					GoogleAnalytics.getInstance().pageview(pre + "disabled", txt);
					stop = true;
				} else if (txt.contains("password") || txt.contains("ended")) {
					stop = true;
				}

				if (stop) {
					ctx.controller.stop();
					return;
				}

				ctx.widgets.component(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click();
				return;
			}

			final String username = account.toString();
			final String password = account.getPassword();
			String text;
			text = getUsernameText();
			if (!text.equalsIgnoreCase(username)) {
				if (!clickLoginInterface(ctx.widgets.component(WIDGET, WIDGET_LOGIN_USERNAME_TEXT))) {
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
				if (!clickLoginInterface(ctx.widgets.component(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT))) {
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

			ctx.widgets.component(WIDGET, WIDGET_LOGIN_BUTTON).click();
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
		if (i.index() == WIDGET_LOGIN_PASSWORD_TEXT) {
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
		for (int i = 0; i < ctx.widgets.component(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).text().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + Random.nextInt(-dx, dx);
		}
	}

	private String getUsernameText() {
		return ctx.widgets.component(WIDGET, WIDGET_LOGIN_USERNAME_TEXT).text().toLowerCase();
	}

	public String getPasswordText() {
		return ctx.widgets.component(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).text();
	}
}
