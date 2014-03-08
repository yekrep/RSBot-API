package org.powerbot.bot.rs3.daemon;

import org.powerbot.misc.GameAccounts;
import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.rs3.tools.ClientContext;

public class BankPin extends PollingScript<ClientContext> implements InternalScript {
	private static final int SETTING_PIN_STEP = 163;
	private static final int WIDGET = 13;
	private static final int COMPONENT = 0;
	private static final int COMPONENT_PIN_OFFSET = 7;

	public BankPin() {
		priority.set(2);
	}

	@Override
	public void poll() {
		if (!ctx.widgets.get(WIDGET, COMPONENT).isVisible()) {
			priority.set(0);
			return;
		}
		priority.set(2);

		final String pin = getPin();
		if (pin == null) {
			ctx.controller.stop();
			return;
		}

		final int i = ctx.settings.get(SETTING_PIN_STEP);
		int v;
		try {
			v = Integer.valueOf(String.valueOf(pin.charAt(i)));
		} catch (final NumberFormatException ignored) {
			v = -1;
		}
		if (v < 0) {
			return;
		}
		if (ctx.widgets.get(WIDGET, v + COMPONENT_PIN_OFFSET).interact("Select")) {
			for (int d = 0; d < 24 && i == ctx.settings.get(SETTING_PIN_STEP); d++) {
				try {
					Thread.sleep(90);
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}

	private String getPin() {
		final GameAccounts.Account account = GameAccounts.getInstance().get(ctx.property(Login.LOGIN_USER_PROPERTY));
		if (account != null) {
			final String pin = account.getPIN();
			if (pin != null && pin.length() == 4) {
				return pin;
			}
		}
		return null;
	}
}
