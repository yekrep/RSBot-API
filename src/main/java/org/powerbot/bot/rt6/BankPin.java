package org.powerbot.bot.rt6;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.*;

public class BankPin extends PollingScript<ClientContext> {
	public BankPin() {
		priority.set(2);
	}

	@Override
	public void poll() {
		if (!ctx.widgets.component(Constants.BANKPIN_WIDGET, Constants.BANKPIN_COMPONENT).visible()) {
			threshold.remove(this);
			return;
		}
		threshold.add(this);

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
