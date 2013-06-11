package org.powerbot.script.randoms;

import org.powerbot.bot.Bot;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.event.PaintListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.framework.ScriptDefinition;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Keyboard;
import org.powerbot.script.methods.Mouse;
import org.powerbot.script.methods.Widgets;
import org.powerbot.script.methods.Lobby;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Manifest(name = "Login", authors = {"Timer"}, description = "Enters account credentials to the login screen")
public class Login extends PollingScript implements RandomEvent, PaintListener {
	private static final int WIDGET = 596;
	private static final int WIDGET_LOGIN_ERROR = 13;
	private static final int WIDGET_LOGIN_TRY_AGAIN = 65;
	private static final int WIDGET_LOGIN_USERNAME_TEXT = 70;
	private static final int WIDGET_LOGIN_PASSWORD_TEXT = 76;
	private static final int WIDGET_LOBBY = 906;
	private static final int WIDGET_LOBBY_ERROR = 249;
	private static final int WIDGET_LOBBY_TRY_AGAIN = 259;
	private final Bot bot;
	private volatile Timer re_load_timer = null;
	private Script wc;

	public Login() {
		this.bot = Bot.getInstance();
	}

	private void lock(final boolean b) {
		if (b) {
			getScriptController().getLockQueue().offer(this);
			getScriptController().getLockQueue().offer(wc);
		} else {
			getScriptController().getLockQueue().remove(this);
			getScriptController().getLockQueue().remove(wc);
		}
	}

	@Override
	public int poll() {
		if (wc == null) {
			for (final ScriptDefinition def : getScriptController().getScripts()) {
				if (def.getScript() instanceof WidgetCloser) {
					this.wc = def.getScript();
					break;
				}
			}
		}

		lock(true);
		final int state = Game.getClientState();
		if ((state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOGGING_IN) && bot.getAccount() != null) {
			Tracker.getInstance().trackPage("randoms/Login/", "Login");
			for (final LoginEvent loginEvent : LoginEvent.values()) {
				final Component Component = Widgets.get(WIDGET, loginEvent.child);
				if (Component != null && Component.isValid()) {
					final String text = Component.getText().toLowerCase().trim();
					Widgets.get(WIDGET, WIDGET_LOGIN_TRY_AGAIN).click(true);

					if (text.contains(loginEvent.message.toLowerCase())) {
						log.info("Handling login event: " + loginEvent.name());
						boolean set_timer = loginEvent.equals(LoginEvent.TOKEN_FAILURE);

						if (set_timer && loginEvent.wait > 0) {
							re_load_timer = new Timer(loginEvent.wait);
						}
						if (loginEvent.wait > 0) {
							sleep(loginEvent.wait);
						} else if (loginEvent.wait == -1) {
							getScriptController().stop();
							lock(false);
							return -1;
						}

						re_load_timer = null;
						if (loginEvent.task != null) {
							try {
								loginEvent.task.get();
							} catch (final InterruptedException | ExecutionException ignored) {
							}
						}
						lock(false);
						return 0;
					}
				}
			}

			if (isUsernameCorrect() && isPasswordValid()) {
				Keyboard.send("\n");
				sleep(Random.nextInt(1200, 2000));
			} else if (!isUsernameCorrect()) {
				final String username = bot.getAccount().toString();
				final Component usernameTextBox = Widgets.get(WIDGET, WIDGET_LOGIN_USERNAME_TEXT);
				if (!clickLoginInterface(usernameTextBox)) {
					lock(false);
					return 0;
				}
				sleep(Random.nextInt(500, 700));
				final int textLength = usernameTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					lock(false);
					return 0;
				}
				Keyboard.send(username);
				sleep(Random.nextInt(500, 700));
			} else if (!isPasswordValid()) {
				final String password = bot.getAccount().getPassword();
				final Component passwordTextBox = Widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT);
				if (!clickLoginInterface(passwordTextBox)) {
					lock(false);
					return 0;
				}
				sleep(Random.nextInt(500, 700));
				final int textLength = passwordTextBox.getText().length();
				if (textLength > 0) {
					erase(textLength);
					lock(false);
					return 0;
				}
				Keyboard.send(password);
				sleep(Random.nextInt(500, 700));
			}
		} else if (state == Game.INDEX_LOBBY_SCREEN && bot.getAccount() != null) {
			Tracker.getInstance().trackPage("randoms/Login/", "Lobby");
			for (final LobbyEvent lobbyEvent : LobbyEvent.values()) {
				final Component Component = Widgets.get(WIDGET_LOBBY, lobbyEvent.child);
				if (Component != null && Component.isValid()) {
					final String text = Component.getText().toLowerCase().trim();

					if (text.contains(lobbyEvent.message.toLowerCase())) {
						log.info("Handling lobby event: " + lobbyEvent.name());
						Widgets.get(WIDGET_LOBBY, WIDGET_LOBBY_TRY_AGAIN).click(true);

						if (lobbyEvent.wait > 0) {
							sleep(lobbyEvent.wait);
						} else if (lobbyEvent.wait == -1) {
							bot.stopScripts();
							lock(false);
							return -1;
						}

						if (lobbyEvent.task != null) {
							try {
								lobbyEvent.task.get();
							} catch (final InterruptedException | ExecutionException ignored) {
							}
						}
						lock(false);
						return 0;
					}
				}
			}

			final int world = ClientFactory.getFactory().preferredWorld;
			if (world > 0) {
				final Lobby.World world_wrapper;
				if ((world_wrapper = Lobby.getWorld(world)) != null) {
					Lobby.enterGame(world_wrapper);
					lock(false);
					return 0;
				}
			}
			Lobby.enterGame();
		}
		lock(false);
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
			return Mouse.click(getPasswordX(i), midy + Random.nextInt(-dy, dy), true);
		}
		return Mouse.click(midx + Random.nextInt(1, maxRandomX), midy + Random.nextInt(-dy, dy), true);
	}

	private int getPasswordX(final Component a) {
		int x = 0;
		final Rectangle pos = a.getBoundingRect();
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
		final String s = bot.getAccount().getPassword();
		return Widgets.get(WIDGET, WIDGET_LOGIN_PASSWORD_TEXT).getText().length() == (s == null ? 0 : s.length());
	}

	private void erase(final int count) {
		for (int i = 0; i <= count + Random.nextInt(1, 5); i++) {
			Keyboard.send("\b");
			if (Random.nextInt(0, 2) == 1) {
				sleep(Random.nextInt(25, 100));
			}
		}
	}

	@Override
	public void onRepaint(final Graphics render) {
		if (re_load_timer != null) {
			render.setColor(Color.white);
			render.drawString("Reloading game in: " + re_load_timer.toRemainingString(), 8, 30);
		}
	}

	private enum LoginEvent {
		TOKEN_FAILURE(WIDGET_LOGIN_ERROR, "game session", 1000 * 5 * 60, new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				Bot.getInstance().refresh();
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