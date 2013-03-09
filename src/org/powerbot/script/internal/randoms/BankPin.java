package org.powerbot.script.internal.randoms;

import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;
import org.powerbot.script.xenon.util.Random;

@Manifest(name = "Bank Pin", authors = {"Timer"}, version = 1.0, description = "Enters the bank pin.")
public class BankPin extends AntiRandom {
	@Override
	public boolean valid() {
		final WidgetChild pinInterface = Widgets.get(13, 0);
		return pinInterface != null && pinInterface.visible();
	}

	@Override
	public int loop() {
		if (!valid()) return -1;

		final String _pin = getPin();
		if (_pin == null) {
			getContainer().stop();
			return -1;
		}
		final String pin = String.format(_pin);
		final int value = Integer.valueOf(String.valueOf(pin.charAt(Settings.get(163))));
		if (value != 4 && Widgets.get(13, value + 6).interact("Select")) {
			return Random.nextInt(700, 1200);
		}
		return 0;
	}

	private String getPin() {
		try {
			return Context.resolve().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}
