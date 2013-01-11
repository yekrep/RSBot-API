package org.powerbot.core.randoms;

import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;

@Manifest(name = "Bank Pin", authors = {"Timer"}, version = 1.0, description = "Enters the bank pin.")
public class BankPin extends AntiRandom {
	@Override
	public boolean activate() {
		final WidgetChild pinInterface = Widgets.get(13, 0);
		return pinInterface != null && pinInterface.visible();
	}

	@Override
	public void execute() {
		final String pin = String.format(getPin());
		final int value = Integer.valueOf(String.valueOf(pin.charAt(Settings.get(163))));
		if (value != 4 && Widgets.get(13, value + 6).interact("Select")) {
			sleep(700, 1000);
		}
	}

	private String getPin() {
		try {
			return Context.resolve().getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}
