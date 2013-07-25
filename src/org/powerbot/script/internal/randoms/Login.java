package org.powerbot.script.internal.randoms;

import java.awt.Rectangle;
import java.util.concurrent.ExecutionException;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Lobby;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;

/**
 * @author Timer
 */
public class Login extends PollingScript implements InternalScript {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 50;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 81;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 83;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 86;
	private static final int WIDGET_LOBBY = 906;
	private static final int WIDGET_LOBBY_TRY_AGAIN = 567;

	public boolean isValid() {
		int state = ctx.game.getClientState();
		return (state == -1 || state == Game.INDEX_LOGIN_SCREEN ||
				state == Game.INDEX_LOBBY_SCREEN ||
				state == Game.INDEX_LOGGING_IN) &&
				ctx.getBot().getAccount() != null;
	}

	@Override
	public int poll() {
		if (!isValid()) {
			return -1;
		}

		int state = ctx.game.getClientState();
		if ((state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN) && ctx.getBot().getAccount() != null) {
			Component c = ctx.widgets.get(WIDGET, WIDGET_LOGIN_ERROR);
			if (c.isValid()) {
				ctx.widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click(true);
				if (c.getText().toLowerCase().contains("password")) {
					getController().stop();
					return -1;
				}
			}

			if (isUsernameCorrect() && isPasswordValid()) {
				ctx.keyboard.send("\n");
				sleep(Random.nextInt(1200, 2000));
			} else if (!isUsernameCorrect()) {
				final String username = ctx.getBot().getAccount().toString();
				final Component usernameTextBox = ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT);
				if (!clickLoginInterface(usernameTextBox)) {
					return 0;
				}
				sleep(Random.nextInt(500, 700));
				final int textLength = usernameTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					return 0;
				}
				ctx.keyboard.send(username);
				sleep(Random.nextInt(500, 700));
			} else if (!isPasswordValid()) {
				final String password = ctx.getBot().getAccount().getPassword();
				final Component passwordTextBox = ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT);
				if (!clickLoginInterface(passwordTextBox)) {
					return 0;
				}
				sleep(Random.nextInt(500, 700));
				final int textLength = passwordTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					return 0;
				}
				ctx.keyboard.send(password);
				sleep(Random.nextInt(500, 700));
			}
		} else if (state == Game.INDEX_LOBBY_SCREEN && ctx.getBot().getAccount() != null) {
			final int world = ctx.getPreferredWorld();
			if (world > 0) {
				final Lobby.World world_wrapper;
				if ((world_wrapper = ctx.lobby.getWorld(world)) != null) {
					ctx.lobby.enterGame(world_wrapper);
					return 0;
				}
			}
			ctx.lobby.enterGame();
		}
		return 600;
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

	private boolean isUsernameCorrect() {
		final String userName = ctx.getBot().getAccount().toString();
		return ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT).getText().toLowerCase().equalsIgnoreCase(userName);
	}

	private boolean isPasswordValid() {
		final String s = ctx.getBot().getAccount().getPassword();
		return ctx.widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText().length() == (s == null ? 0 : s.length());
	}

	private void erase(final int count) {
		for (int i = 0; i <= count + Random.nextInt(1, 5); i++) {
			ctx.keyboard.send("\b");
			if (Random.nextInt(0, 2) == 1) {
				sleep(Random.nextInt(25, 100));
			}
		}
	}
}