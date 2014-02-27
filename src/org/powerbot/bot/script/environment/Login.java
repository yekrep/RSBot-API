package org.powerbot.bot.script.environment;

import java.awt.Rectangle;

import org.powerbot.misc.GameAccounts;
import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.rs3.tools.Game;
import org.powerbot.script.rs3.tools.Lobby;
import org.powerbot.script.util.Random;
import org.powerbot.script.rs3.tools.Component;

/**
 * @author Timer
 */
public class Login extends PollingScript implements InternalScript {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 57;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 84;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 90;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 93;

	public static final String LOGIN_USER_PROPERTY = "login.account.username";

	public Login() {
		priority.set(4);
	}

	private boolean isValid() {
		if (ctx.properties.getProperty("login.disable", "").trim().equalsIgnoreCase("true")) {
			return false;
		}

		final int state = ctx.game.getClientState();
		return state == -1 || state == Game.INDEX_LOGIN_SCREEN ||
				state == Game.INDEX_LOBBY_SCREEN ||
				state == Game.INDEX_LOGGING_IN;
	}

	@Override
	public int poll() {
		if (!isValid()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());

		final GameAccounts.Account account = GameAccounts.getInstance().get(ctx.properties.getProperty(LOGIN_USER_PROPERTY));
		final int state = ctx.game.getClientState();

		if (state == Game.INDEX_LOBBY_SCREEN) {
			int world = -1;
			final String k = "login.world";
			if (ctx.properties.containsKey(k)) {
				try {
					world = Integer.parseInt(ctx.properties.getProperty(k));
				} catch (final NumberFormatException ignored) {
				}
			}

			final Component child = ctx.widgets.get(906, 517); // post email validation continue button
			if (child.isVisible()) {
				child.click();
				return -1;
			}

			if (world > 0) {
				final Lobby.World world_wrapper;
				if ((world_wrapper = ctx.lobby.getWorld(world)) != null) {
					if (!ctx.lobby.enterGame(world_wrapper) && account != null) {
						final Lobby.World[] worlds = ctx.lobby.getWorlds(new Filter<Lobby.World>() {
							@Override
							public boolean accept(final Lobby.World world) {
								return world.isMembers() == account.member;
							}
						});
						if (worlds.length > 0) {
							ctx.properties.setProperty("login.world", Integer.toString(worlds[Random.nextInt(0, worlds.length)].getNumber()));
						}
					}
					return 0;
				}
			}
			ctx.lobby.enterGame();
			return -1;
		}

		if (account != null && (state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN)) {
			final Component error = ctx.widgets.get(WIDGET, WIDGET_LOGIN_ERROR);
			if (error.isVisible()) {
				if (error.getText().toLowerCase().contains("password") ||
						error.getText().toLowerCase().contains("ended")) {
					getController().stop();
					return -1;
				}
				ctx.widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click();
				return -1;
			}

			final String username = account.toString();
			final String password = account.getPassword();
			String text;
			text = getUsernameText();
			if (!text.equalsIgnoreCase(username)) {
				if (!clickLoginInterface(ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT))) {
					return -1;
				}
				sleep(600);

				final int length = text.length();
				if (length > 0) {
					final StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.keyboard.send(b.toString());
					return 0;
				}

				ctx.keyboard.send(username);
				sleep(1000);
				return 0;
			}

			text = getPasswordText();
			if (text.length() != password.length()) {
				if (!clickLoginInterface(ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT))) {
					return -1;
				}
				sleep(600);
				final int length = text.length();
				if (length > 0) {
					final StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.keyboard.send(b.toString());
					return -1;
				}
				ctx.keyboard.send(password);
				return -1;
			}

			ctx.keyboard.send("\n");
			sleep(1200);
			return -1;
		}
		return -1;//what's going on???
	}

	private boolean clickLoginInterface(final Component i) {
		if (!i.isValid()) {
			return false;
		}
		final Rectangle pos = i.getBoundingRect();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		final int midx = (int) pos.getCenterX();
		final int h = (int) pos.getHeight();
		final int midy = (int) (pos.getMinY() + (h == 0 ? 27 : h) / 2);
		if (i.getIndex() == WIDGET_LOGIN_PASSWORD_TEXT) {
			return ctx.mouse.click(getPasswordX(i), midy + Random.nextInt(-dy, dy), true);
		}
		return ctx.mouse.click(midx + Random.nextInt(1, maxRandomX), midy + Random.nextInt(-dy, dy), true);
	}

	private int getPasswordX(final Component a) {
		int x = 0;
		final Rectangle pos = a.getBoundingRect();
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return 0;
		}
		for (int i = 0; i < ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + Random.nextInt(-dx, dx);
		}
	}

	private String getUsernameText() {
		return ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT).getText().toLowerCase();
	}

	public String getPasswordText() {
		return ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText();
	}
}
