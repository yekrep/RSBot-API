package org.powerbot.bot.rt6;

import org.powerbot.misc.GameAccounts;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;

public class BankPin extends PollingScript<ClientContext> {
	private static final int SETTING_PIN_STEP = 163;
	private static final int WIDGET = 13;
	private static final int COMPONENT = 0;
	private static final int COMPONENT_PIN_OFFSET = 7;

	public BankPin() {
		priority.set(2);
	}

	@Override
	public void poll() {
		if (!ctx.widgets.component(WIDGET, COMPONENT).visible()) {
			if (threshold.contains(this)) {
				threshold.remove(this);
			}
			return;
		}
		if (!threshold.contains(this)) {
			threshold.add(this);
		}

		final String pin = getPin();
		if (pin == null) {
			ctx.controller.stop();
			return;
		}

		final int i = ctx.varpbits.varpbit(SETTING_PIN_STEP);
		int v;
		try {
			v = Integer.valueOf(String.valueOf(pin.charAt(i)));
		} catch (final NumberFormatException ignored) {
			v = -1;
		}
		if (v < 0) {
			return;
		}
		if (ctx.widgets.component(WIDGET, v + COMPONENT_PIN_OFFSET).interact("Select")) {
			for (int d = 0; d < 24 && i == ctx.varpbits.varpbit(SETTING_PIN_STEP); d++) {
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
