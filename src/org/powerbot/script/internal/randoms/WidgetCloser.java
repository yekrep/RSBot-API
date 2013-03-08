package org.powerbot.script.internal.randoms;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.script.PollingTaskScript;
import org.powerbot.script.task.AsyncTask;

@RandomManifest(name = "Widget closer")
public class WidgetCloser extends PollingTaskScript {

	private static final int[] COMPONENTS = {
		21 << 16 | 43, // beholding a player's statuette (duellist's cap)
		1234 << 16 | 15, // membership offers
		906 << 16 | 354, // membership offers
		906 << 16 | 493, // email register
		1252 << 16 | 8, // Squeal of Fortune notification
		1253 << 16 | 16, // Squeal of Fortune window
		1218 << 16 | 77, // advanced skill guide
		1107 << 16 | 157, // clan popups
		755 << 16 | 44, // world map
	};

	public WidgetCloser() {
		submit(new Task());
	}

	private final class Task extends AsyncTask {

		@Override
		public boolean isValid() {
			for (final int p : COMPONENTS) {
				final WidgetChild child = Widgets.get(p >> 16, p & 0xffff);
				if (child != null && child.validate()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void run() {
			for (final int p : COMPONENTS) {
				final WidgetChild child = Widgets.get(p >> 16, p & 0xffff);
				if (child != null && child.validate()) {
					child.click(true);
				}
			}
		}
	}
}
