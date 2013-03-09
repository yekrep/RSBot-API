package org.powerbot.script.internal.randoms;

import org.powerbot.script.TaskScript;
import org.powerbot.script.task.AsyncTask;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;

@RandomManifest(name = "Bank Pin")
public class BankPin extends TaskScript {

	public BankPin() {
		submit(new Task());
	}

	private final class Task extends AsyncTask {

		@Override
		public boolean isValid() {
			final WidgetChild pinInterface = Widgets.get(13, 0);
			return pinInterface != null && pinInterface.visible() && getPin() != null;
		}

		@Override
		public void run() {
			final String pin = String.format(getPin());
			final int value = Integer.valueOf(String.valueOf(pin.charAt(Settings.get(163))));
			if (value != 4) {
				Widgets.get(13, value + 6).interact("Select");
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
