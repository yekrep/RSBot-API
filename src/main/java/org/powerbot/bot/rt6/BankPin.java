package org.powerbot.bot.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;

public class BankPin extends PollingScript<ClientContext> {
	public BankPin() {
		priority.set(2);
	}

	@Override
	public void poll() {
		if (!ctx.widgets.component(Constants.BANKPIN_WIDGET, Constants.BANKPIN_COMPONENT).visible()) {
			if (threshold.contains(this)) {
				threshold.remove(this);
			}
			return;
		}
		if (!threshold.contains(this)) {
			threshold.add(this);
		}

		final String pin = ctx.getPin();
		if (pin == null) {
			ctx.controller.stop();
			return;
		}

		final int i = ctx.varpbits.varpbit(Constants.BANKPIN_PIN_STATE);
		int v;
		try {
			v = Integer.valueOf(String.valueOf(pin.charAt(i)));
		} catch (final NumberFormatException ignored) {
			v = -1;
		}
		if (v < 0) {
			return;
		}
		if (ctx.widgets.component(Constants.BANKPIN_WIDGET, v + Constants.BANKPIN_PIN).interact("Select")) {
			for (int d = 0; d < 24 && i == ctx.varpbits.varpbit(Constants.BANKPIN_PIN_STATE); d++) {
				Condition.sleep(100);
			}
		}
	}
}
