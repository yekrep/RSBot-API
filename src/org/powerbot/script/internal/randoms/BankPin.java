package org.powerbot.script.internal.randoms;

import org.powerbot.game.bot.Context;
import org.powerbot.script.Manifest;
import org.powerbot.script.TaskScript;
import org.powerbot.script.task.AsyncTask;
import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.wrappers.Component;

@Manifest(name = "Bank Pin", authors = {"Timer"}, description = "Enters the stored bank pin")
public class BankPin extends TaskScript {
	public BankPin() {
		submit(new Task());
	}

	private final class Task extends AsyncTask {
		@Override
		public boolean isValid() {
			final Component pinInterface = Widgets.get(13, 0);
			return pinInterface != null && pinInterface.isVisible();
		}

		@Override
		public void run() {
			final String _pin = getPin();
			if (_pin == null) {
				getScriptController().stop();
				return;
			}
			final String pin = String.format(_pin);
			final int value = Integer.valueOf(String.valueOf(pin.charAt(Settings.get(163))));
			if (value != 4 && Widgets.get(13, value + 6).interact("Select")) {
				Delay.sleep(Random.nextInt(700, 1200));
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
}