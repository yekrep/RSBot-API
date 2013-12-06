package org.powerbot.script.internal.scripts;

import org.powerbot.bot.Bot;
import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Random;
import org.powerbot.service.GameAccounts;

public class BankPin extends PollingScript implements InternalScript {
	private static final int SETTING_PIN_STEP = 163;
	private static final int WIDGET = 13;
	private static final int COMPONENT = 0;
	private static final int COMPONENT_PIN_OFFSET = 6;

	public BankPin() {
		priority.set(2);
	}

	@Override
	public int poll() {
		if (!ctx.widgets.get(WIDGET, COMPONENT).isVisible()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());

		final String pin = getPin();
		if (pin == null) {
			getController().stop();
			return -1;
		}

		final int i = ctx.settings.get(SETTING_PIN_STEP);
		int v;
		try {
			v = Integer.valueOf(String.valueOf(pin.charAt(i)));
		} catch (NumberFormatException ignored) {
			v = -1;
		}
		if (v < 0) {
			return -1;
		}
		if (ctx.widgets.get(WIDGET, v + COMPONENT_PIN_OFFSET).interact("Select")) {
			for (int d = 0; d < 24 && i == ctx.settings.get(SETTING_PIN_STEP); d++) {
				sleep(Random.nextInt(80, 100));
			}
		}
		return i != ctx.settings.get(SETTING_PIN_STEP) ? Random.nextInt(600, 1800) : 100;
	}

	private String getPin() {
		final Bot bot = ctx.getBot();
		final GameAccounts.Account account = GameAccounts.getInstance().get(ctx.properties.getProperty(Login.LOGIN_USER_PROPERTY));
		if (bot != null && account != null) {
			final String pin = account.getPIN();
			if (pin != null && pin.length() == 4) {
				return pin;
			}
		}
		return null;
	}
}
