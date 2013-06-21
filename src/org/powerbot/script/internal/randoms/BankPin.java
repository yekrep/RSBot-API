package org.powerbot.script.internal.randoms;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;

/**
 * @author Paris
 * @author Timer
 */
public class BankPin extends PollingScript implements InternalScript {
	private final int PIN_INDEX = 163, PIN_WIDGET = 13, PIN_WIDGET_BASE = 0, PIN_WIDGET_OFFSET = 6;

	@Override
	public int poll() {
		final String pin = getPin();

		while (ctx.widgets.get(PIN_WIDGET, PIN_WIDGET_BASE).isVisible()) {
			if (pin == null) {
				getController().stop();
				return -1;
			}

			int i, v = Integer.valueOf(String.valueOf(pin.charAt(i = ctx.settings.get(PIN_INDEX))));
			if (ctx.widgets.get(PIN_WIDGET, v + PIN_WIDGET_OFFSET).interact("Select")) {
				while (i == ctx.settings.get(PIN_INDEX)) {
					sleep(2400);
				}
			}

			sleep(600);
		}

		return -1;
	}

	private String getPin() {
		try {
			return ctx.getBot().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}
