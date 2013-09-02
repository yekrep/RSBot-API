package org.powerbot.script.internal.randoms;

import java.awt.Rectangle;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Lobby;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.service.GameAccounts;

/**
 * @author Timer
 */
public class Login extends PollingScript implements InternalScript {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 57;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 84;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 90;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 93;

	@Override
	public boolean isValid() {
		int state = ctx.game.getClientState();
		return state == -1 || state == Game.INDEX_LOGIN_SCREEN ||
				state == Game.INDEX_LOBBY_SCREEN ||
				state == Game.INDEX_LOGGING_IN;
	}

	@Override
	public int poll() {
		if (!isValid() || ctx.getBreakManager().isBreaking()) {
			return -1;
		}

		final GameAccounts.Account account = ctx.getBot().getAccount();
		int state = ctx.game.getClientState();
		if (state == Game.INDEX_LOBBY_SCREEN) {
			int world = ctx.getPreferredWorld();
			if (world > 0) {
				Lobby.World world_wrapper;
				if ((world_wrapper = ctx.lobby.getWorld(world)) != null) {
					if (!ctx.lobby.enterGame(world_wrapper) && account != null) {
						Lobby.World[] worlds = ctx.lobby.getWorlds(new Filter<Lobby.World>() {
							@Override
							public boolean accept(Lobby.World world) {
								return world.isMembers() == account.member;
							}
						});
						if (worlds.length > 0) {
							ctx.game.setPreferredWorld(worlds[Random.nextInt(0, worlds.length)].getNumber());
						}
					}
					return 0;
				}
			}
			ctx.lobby.enterGame();
			return -1;
		}

		if (account != null && (state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN)) {
			Component error = ctx.widgets.get(WIDGET, WIDGET_LOGIN_ERROR);
			if (error.isValid()) {
				if (error.getText().toLowerCase().contains("password")) {
					getController().stop();
					return -1;
				}
				ctx.widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click();
				return -1;
			}

			String username = account.toString(), password = account.getPassword();
			String text;
			text = getUsernameText();
			if (!text.equalsIgnoreCase(username)) {
				if (!clickLoginInterface(ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT))) {
					return -1;
				}
				sleep(Random.nextInt(500, 700));

				int length = text.length();
				if (length > 0) {
					StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.keyboard.send(b.toString());
					return 0;
				}

				ctx.keyboard.send(username);
				sleep(Random.nextInt(800, 1200));
				return 0;
			}

			text = getPasswordText();
			if (text.length() != password.length()) {
				if (!clickLoginInterface(ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT))) {
					return -1;
				}
				sleep(Random.nextInt(500, 700));
				int length = text.length();
				if (length > 0) {
					StringBuilder b = new StringBuilder(length);
					for (int i = 0; i < length; i++) {
						b.append('\b');
					}
					ctx.keyboard.send(b.toString());
					return 0;
				}
				ctx.keyboard.send(password);
				return -1;
			}

			ctx.keyboard.send("\n");
			sleep(Random.nextInt(600, 1400));
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