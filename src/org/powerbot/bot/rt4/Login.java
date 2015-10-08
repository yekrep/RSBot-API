package org.powerbot.bot.rt4;

import java.awt.Rectangle;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.misc.GameAccounts;
import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

public class Login extends PollingScript<ClientContext> {
	public static final String LOGIN_USER_PROPERTY = "login.account.username";
	private static final int
			CLIENT_STATE_MAIN = 10,
			CLIENT_STATE_LOGGING = 20,
			CLIENT_STATE_LOADING = 25,
			CLIENT_STATE_LOADED = 30;

	public Login() {
		priority.set(4);
	}

	private boolean isValid() {
		final Client c = ctx.client();
		return c != null &&
				!(ctx.properties.getProperty("login.disable", "").equals("true") && c.getClientState() <= CLIENT_STATE_LOGGING) &&
				!(ctx.properties.getProperty("lobby.disable", "").equals("true") && c.getClientState() > CLIENT_STATE_LOGGING) &&
				(c.getClientState() >= CLIENT_STATE_MAIN && c.getClientState() < CLIENT_STATE_LOADED || isLobby());
	}

	private boolean isLobby() {
		return ctx.widgets.component(378, 6).valid();
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
		final Client c = ctx.client();
		final GameAccounts.Account account = GameAccounts.getInstance().get(ctx.properties.getProperty(LOGIN_USER_PROPERTY, ""));
		if (account == null || account.getPassword().isEmpty()) {
			return;
		}
		if (c.getClientState() >= CLIENT_STATE_LOGGING) {
			if (isLobby()) {
				ctx.widgets.component(378, 6).click();
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return !ctx.widgets.component(378, 6).valid();
					}
				});
			}
			return;
		}

		//if (c.isWorldSelectionUp()) {
		//	ctx.input.click(735, 12, true);
		//	return;
		//}

		switch (c.getLoginState()) {
		case 0: {
			final Rectangle existing_user = new Rectangle(400, 275, 130, 25);
			ctx.input.click(
					existing_user.x + Random.nextGaussian(0, existing_user.width, existing_user.width / 2),
					existing_user.y + Random.nextGaussian(0, existing_user.height, existing_user.height / 2),
					true
			);
			break;
		}
		case 2: {
			final String u_ = account.toString(), p_ = account.getPassword(),
					u = c.getUsername(), p = c.getPassword();

			if (!u.equalsIgnoreCase(u_)) {
				if (c.getLoginField() != 0) {
					ctx.input.send("{TAB}");
				}
				if (!Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return c.getLoginField() == 0;
					}
				}, 10, 10)) {
					return;
				}
				if (!u.isEmpty()) {
					final StringBuilder b = new StringBuilder();
					for (int i = 0; i < u.length(); ++i) {
						b.append('\b');
					}
					ctx.input.send(b.toString());
					return;
				}
				ctx.input.sendln(u_);
			} else if (!p.equals(p_)) {
				if (c.getLoginField() != 1) {
					ctx.input.send("{TAB}");
				}
				if (!Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return c.getLoginField() == 1;
					}
				}, 10, 10)) {
					return;
				}
				if (!p.isEmpty()) {
					final StringBuilder b = new StringBuilder();
					for (int i = 0; i < p.length(); ++i) {
						b.append('\b');
					}
					ctx.input.send(b.toString());
					return;
				}
				ctx.input.send(p_);
			} else {
				ctx.input.sendln("");
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return isLobby();
					}
				}, 100, 100);
				return;
			}
			break;
		}
		default: {
			System.out.printf("Unknown login state (%d).%n", c.getLoginState());
			break;
		}
		}
	}
}