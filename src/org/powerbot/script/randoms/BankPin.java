package org.powerbot.script.randoms;

import org.powerbot.bot.Bot;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Settings;
import org.powerbot.script.methods.Widgets;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Bank Pin", authors = {"Timer"}, description = "Enters the stored bank pin")
public class BankPin extends PollingScript implements RandomEvent {
	@Override
	public int poll() {
		final Component pinInterface = Widgets.get(13, 0);
		if (pinInterface == null || !pinInterface.isVisible()) return 600;
		Tracker.getInstance().trackPage("randoms/BankPin/", "");
		getScriptController().getLockQueue().offer(this);
		final String _pin = getPin();
		if (_pin == null) {
			getScriptController().stop();
			getScriptController().getLockQueue().remove(this);
			return -1;
		}
		final String pin = String.format(_pin);
		final int value = Integer.valueOf(String.valueOf(pin.charAt(Settings.get(163))));
		if (Widgets.get(13, value + 6).interact("Select")) {
			Delay.sleep(Random.nextInt(700, 1200));
		}
		getScriptController().getLockQueue().remove(this);
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