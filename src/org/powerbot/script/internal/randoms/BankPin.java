package org.powerbot.script.internal.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.internal.ScriptGroup;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Bank Pin", authors = {"Timer"}, description = "Enters the stored bank pin")
public class BankPin extends PollingPassive {
	public BankPin(MethodContext ctx, ScriptGroup container) {
		super(ctx, container);
	}

	@Override
	public boolean isValid() {
		Component pinInterface = ctx.widgets.get(13, 0);
		return pinInterface != null && pinInterface.isVisible();
	}

	@Override
	public int poll() {
		Component pinInterface = ctx.widgets.get(13, 0);
		if (pinInterface == null || !pinInterface.isVisible()) {
			return -1;
		}
		Tracker.getInstance().trackPage("randoms/BankPin/", "");
		String _pin = getPin();
		if (_pin == null) {
			getContainer().stop();
			return -1;
		}
		String pin = String.format(_pin);
		int setting;
		int value = Integer.valueOf(String.valueOf(pin.charAt(setting = ctx.settings.get(163))));
		if (ctx.widgets.get(13, value + 6).interact("Select")) {
			for (int i = 0; i < 40 && setting == ctx.settings.get(163); i++) {
				sleep(500, 1000);
			}
		}
		return Random.nextInt(700, 1200);
	}

	private String getPin() {
		try {
			return ctx.bot.getAccount().getPIN();
		} catch (final Exception ignored) {
		}
		return null;
	}
}