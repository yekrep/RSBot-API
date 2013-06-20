package org.powerbot.script.internal.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Bank Pin", authors = {"Timer"}, description = "Enters the stored bank pin")
public class BankPin extends PollingScript implements InternalScript {

	@Override
	public int poll() {
		final Component pinInterface = ctx.widgets.get(13, 0);
		if (pinInterface == null || !pinInterface.isVisible()) {
			return -1;
		}

		Tracker.getInstance().trackPage("randoms/BankPin/", "");

		String pin = getPin();
		if (pin == null) {
			getController().stop();
			return -1;
		}

		int setting;
		int value = Integer.valueOf(String.valueOf(pin.charAt(setting = ctx.settings.get(163))));
		if (ctx.widgets.get(13, value + 6).interact("Select")) {
			for (int i = 0; i < 40 && setting == ctx.settings.get(163); i++) {
				sleep(500, 1000);
			}
		}

		return Random.nextInt(700, 1200);
	}

	@Override
	public int getPriority() {
		return PRIORITY_HIGH;
	}

	private String getPin() {
		try {
			return ctx.getBot().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}
