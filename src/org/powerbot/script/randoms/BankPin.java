package org.powerbot.script.randoms;

import org.powerbot.bot.Bot;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Bank Pin", authors = {"Timer"}, description = "Enters the stored bank pin")
public class BankPin extends PollingScript implements RandomEvent {
	@Override
	public int poll() {
		final Component pinInterface = ctx.widgets.get(13, 0);
		if (pinInterface == null || !pinInterface.isVisible()) {
			return 600;
		}
		Tracker.getInstance().trackPage("randoms/BankPin/", "");
		final String _pin = getPin();
		if (_pin == null) {
			getContainer().stop();
			return -1;
		}
		final String pin = String.format(_pin);
		final int value = Integer.valueOf(String.valueOf(pin.charAt(ctx.settings.get(163))));
		if (ctx.widgets.get(13, value + 6).interact("Select")) {
			Delay.sleep(Random.nextInt(700, 1200));
		}
		return 0;
	}

	private String getPin() {
		try {
			return Bot.getInstance().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}