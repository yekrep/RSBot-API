package org.powerbot.script.internal.randoms;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Random;

public class BankPin extends PollingScript implements InternalScript {
	private static final int SETTING_PIN_STEP = 163;
	private static final int WIDGET = 13;
	private static final int COMPONENT = 0;
	private static final int COMPONENT_PIN_OFFSET = 6;

	@Override
	public int poll() {
		final String pin = getPin();

		if (pin == null) {
			getController().stop();
			return -1;
		}

		if (ctx.widgets.get(WIDGET, COMPONENT).isVisible()) {
			return -1;
		}

		int i, v = Integer.valueOf(String.valueOf(pin.charAt(i = ctx.settings.get(SETTING_PIN_STEP))));
		if (ctx.widgets.get(WIDGET, v + COMPONENT_PIN_OFFSET).interact("Select")) {
			for (int d = 0; d < 24 && i == ctx.settings.get(SETTING_PIN_STEP); d++) {
				sleep(Random.nextInt(80, 100));
			}
		}
		return i != ctx.settings.get(SETTING_PIN_STEP) ? Random.nextInt(600, 1800) : 100;
	}

	private String getPin() {
		try {
			return ctx.getBot().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}
