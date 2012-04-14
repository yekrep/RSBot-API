package org.powerbot.game.bot.randoms;

import java.awt.Rectangle;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;

/**
 * @author Timer
 */
@Manifest(name = "Login", description = "Logs into the game and handles errors", version = 0.1, authors = {"Timer"})
public class Login extends AntiRandom {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 13;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 65;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 70;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 76;
	private static final int WIDGET_LOGIN_ENTER_GAME = 44;

	private static final int WIDGET_LOBBY = 906;
	private static final int WIDGET_LOBBY_PLAY = 184;

	public boolean validate() {
		final int state = Game.getClientState();
		return (state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOBBY_SCREEN || state == Game.INDEX_LOGGING_IN) && bot.getAccount() != null;
	}

	private enum LoginEvent {
		TOKEN_FAILURE(WIDGET_LOGIN_ERROR, "token failure", 0, new Task() {
			public void run() {
				Context.resolve().refresh();
			}
		}),
		INVALID_PASSWORD(WIDGET_LOGIN_ERROR, "Invalid username or password", -1);

		private final String message;
		private final int child, wait;
		private final Task task;

		LoginEvent(final int child, final String message, final int wait, final Task task) {
			this.child = child;
			this.message = message;
			this.wait = wait;
			this.task = task;
		}

		LoginEvent(final int child, final String message, final int wait) {
			this(child, message, wait, null);
		}
	}

	public void run() {
		if (Game.getClientState() == Game.INDEX_LOBBY_SCREEN) {
			Widgets.get(WIDGET_LOBBY, WIDGET_LOBBY_PLAY).click(true);
			Time.sleep(Random.nextInt(200, 500));
			return;
		}

		if (Game.getClientState() == Game.INDEX_LOGIN_SCREEN) {
			for (final LoginEvent loginEvent : LoginEvent.values()) {
				final WidgetChild widgetChild = Widgets.get(WIDGET, loginEvent.child);
				if (widgetChild != null && widgetChild.validate()) {
					final String text = widgetChild.getText().toLowerCase().trim();

					if (text.contains(loginEvent.message.toLowerCase())) {
						log.info("Handling login event: " + loginEvent.name());
						Widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click(true);

						if (loginEvent.wait > 0) {
							Time.sleep(loginEvent.wait);
						} else if (loginEvent.wait == -1) {
							bot.stopScript();
							return;
						}

						if (loginEvent.task != null) {
							bot.getContainer().submit(loginEvent.task);
						}
						return;
					}
				}
			}

			if (isUsernameCorrect() && isPasswordValid()) {
				attemptLogin();
				Time.sleep(Random.nextInt(1200, 2000));
			} else if (!isUsernameCorrect()) {
				final String username = bot.getAccount().toString();
				final WidgetChild usernameTextBox = Widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT);
				if (!clickLoginInterface(usernameTextBox)) {
					return;
				}
				Time.sleep(Random.nextInt(500, 700));
				final int textLength = usernameTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					return;
				}
				Keyboard.sendText(username, false);
				Time.sleep(Random.nextInt(500, 700));
			} else if (!isPasswordValid()) {
				final String password = bot.getAccount().getPassword();
				final WidgetChild passwordTextBox = Widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT);
				if (!clickLoginInterface(passwordTextBox)) {
					return;
				}
				Time.sleep(Random.nextInt(500, 700));
				final int textLength = passwordTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					return;
				}
				Keyboard.sendText(password, false);
				Time.sleep(Random.nextInt(500, 700));
			}
		}
	}

	private boolean clickLoginInterface(final WidgetChild i) {
		if (!i.validate()) {
			return false;
		}
		final Rectangle pos = i.getBoundingRectangle();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		final int midx = (int) pos.getCenterX();
		final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		if (i.getIndex() == WIDGET_LOGIN_PASSWORD_TEXT) {
			return Mouse.click(getPasswordX(i), midy + Random.nextInt(-dy, dy), true);
		}
		return Mouse.click(midx + Random.nextInt(1, maxRandomX), midy + Random.nextInt(-dy, dy), true);
	}

	private int getPasswordX(final WidgetChild a) {
		int x = 0;
		final Rectangle pos = a.getBoundingRectangle();
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return 0;
		}
		for (int i = 0; i < Widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + Random.nextInt(-dx, dx);
		}
	}

	private boolean isUsernameCorrect() {
		final String userName = bot.getAccount().toString();
		return Widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT).getText().toLowerCase().equalsIgnoreCase(userName);
	}

	private boolean isPasswordValid() {
		String passWord = bot.getAccount().getPassword();
		return Widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText().length() == (passWord == null ? 0 : passWord.length());
	}

	private void attemptLogin() {
		if (Random.nextInt(0, 6) == 0) {
			Widgets.get(WIDGET, WIDGET_LOGIN_ENTER_GAME).click(true);
		} else {
			Keyboard.sendKey('\n', Random.nextInt(100, 200));
		}
	}

	private void erase(final int count) {
		for (int i = 0; i <= count + Random.nextInt(1, 5); i++) {
			Keyboard.sendKey('\b', Random.nextInt(50, 150));
			if (Random.nextInt(0, 2) == 1) {
				Time.sleep(Random.nextInt(25, 100));
			}
		}
	}
}
