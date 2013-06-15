package org.powerbot.script.internal.randoms;

import org.powerbot.gui.BotChrome;
import org.powerbot.script.Manifest;
import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Lobby;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Manifest(name = "Login", authors = {"Timer"}, description = "Enters account credentials to the login screen")
public class Login extends PollingPassive {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 13;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 65;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 70;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 76;
	private static final int WIDGET_LOBBY = 906;
	private static final int WIDGET_LOBBY_ERROR = 249;
	private static final int WIDGET_LOBBY_TRY_AGAIN = 259;
	private volatile Timer re_load_timer = null;

	public Login(MethodContext ctx, ScriptContainer container) {
		super(ctx, container);
	}

	@Override
	public boolean isValid() {
		int state = ctx.game.getClientState();
		return (state == Game.INDEX_LOGIN_SCREEN ||
				state == Game.INDEX_LOBBY_SCREEN ||
				state == Game.INDEX_LOGGING_IN) &&
				ctx.bot.getAccount() != null;
	}

	@Override
	public int poll() {
		if (!isValid()) return -1;

		int state = ctx.game.getClientState();
		if ((state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN) && ctx.bot.getAccount() != null) {
			Tracker.getInstance().trackPage("randoms/Login/", "Login");
			for (final LoginEvent loginEvent : LoginEvent.values()) {
				final Component Component = ctx.widgets.get(WIDGET, loginEvent.child);
				if (Component != null && Component.isValid()) {
					final String text = Component.getText().toLowerCase().trim();
					ctx.widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click(true);

					if (text.contains(loginEvent.message.toLowerCase())) {
						log.info("Handling login event: " + loginEvent.name());
						boolean set_timer = loginEvent.equals(LoginEvent.TOKEN_FAILURE);

						if (set_timer && loginEvent.wait > 0) {
							re_load_timer = new Timer(loginEvent.wait);
						}
						if (loginEvent.wait > 0) {
							sleep(loginEvent.wait);
						} else if (loginEvent.wait == -1) {
							getContainer().stop();
							return -1;
						}

						re_load_timer = null;
						if (loginEvent.task != null) {
							try {
								loginEvent.task.get();
							} catch (final InterruptedException | ExecutionException ignored) {
							}
						}
						return 0;
					}
				}
			}

			if (isUsernameCorrect() && isPasswordValid()) {
				ctx.keyboard.send("\n");
				sleep(Random.nextInt(1200, 2000));
			} else if (!isUsernameCorrect()) {
				final String username = ctx.bot.getAccount().toString();
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
				final String password = ctx.bot.getAccount().getPassword();
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
		} else if (state == Game.INDEX_LOBBY_SCREEN && ctx.bot.getAccount() != null) {
			Tracker.getInstance().trackPage("randoms/Login/", "Lobby");
			for (final LobbyEvent lobbyEvent : LobbyEvent.values()) {
				final Component Component = ctx.widgets.get(WIDGET_LOBBY, lobbyEvent.child);
				if (Component != null && Component.isValid()) {
					final String text = Component.getText().toLowerCase().trim();

					if (text.contains(lobbyEvent.message.toLowerCase())) {
						log.info("Handling lobby event: " + lobbyEvent.name());
						ctx.widgets.get(WIDGET_LOBBY, WIDGET_LOBBY_TRY_AGAIN).click(true);

						if (lobbyEvent.wait > 0) {
							sleep(lobbyEvent.wait);
						} else if (lobbyEvent.wait == -1) {
							ctx.bot.stopScripts();
							return -1;
						}

						if (lobbyEvent.task != null) {
							try {
								lobbyEvent.task.get();
							} catch (final InterruptedException | ExecutionException ignored) {
							}
						}
						return 0;
					}
				}
			}

			final int world = ctx.preferredWorld;
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
		final String userName = ctx.bot.getAccount().toString();
		return ctx.widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT).getText().toLowerCase().equalsIgnoreCase(userName);
	}

	private boolean isPasswordValid() {
		final String s = ctx.bot.getAccount().getPassword();
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

	@Override
	public void onRepaint(final Graphics render) {
		super.onRepaint(render);
		if (re_load_timer != null) {
			render.setColor(Color.white);
			render.drawString("Reloading game in: " + re_load_timer.toRemainingString(), 8, 30);
		}
	}

	private enum LoginEvent {
		TOKEN_FAILURE(WIDGET_LOGIN_ERROR, "game session", 1000 * 5 * 60, new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				BotChrome.getInstance().getBot().refresh();
			}
		}, true)),
		INVALID_PASSWORD(WIDGET_LOGIN_ERROR, "Invalid username or password", -1);
		private final String message;
		private final int child, wait;
		private final FutureTask<Boolean> task;

		LoginEvent(final int child, final String message, final int wait, final FutureTask<Boolean> task) {
			this.child = child;
			this.message = message;
			this.wait = wait;
			this.task = task;
		}

		LoginEvent(final int child, final String message, final int wait) {
			this(child, message, wait, null);
		}
	}

	private enum LobbyEvent {
		LOGGED_IN(WIDGET_LOBBY_ERROR, "last session", Random.nextInt(1000, 4000));
		private final String message;
		private final int child, wait;
		private final FutureTask<Boolean> task;

		LobbyEvent(final int child, final String message, final int wait, final FutureTask<Boolean> task) {
			this.child = child;
			this.message = message;
			this.wait = wait;
			this.task = task;
		}

		LobbyEvent(final int child, final String message, final int wait) {
			this(child, message, wait, null);
		}
	}
}