package org.powerbot.script.internal.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.TaskScript;
import org.powerbot.script.task.AsyncTask;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Widget closer", authors = {"Timer"}, description = "Closes widgets")
public class WidgetCloser extends TaskScript {
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
		private final Timer threshold;
		private Component component;
		private int tries;

		private Task() {
			threshold = new Timer(0);
		}

		@Override
		public boolean isValid() {
			if (threshold.isRunning()) return false;
			for (final int p : COMPONENTS) {
				component = Widgets.get(p >> 16, p & 0xffff);
				if (component != null && component.isValid()) {
					break;
				}
			}
			return component != null;
		}

		@Override
		public void run() {
			Tracker.getInstance().trackPage("randoms/WidgetCloser/", "");

			if (++tries > 3) {
				threshold.setEndIn(60000);
				return;
			}

			if (component.isValid() && component.click(true)) {
				final Timer timer = new Timer(Random.nextInt(2000, 2500));
				while (timer.isRunning() && component.isValid()) sleep(100, 250);
				if (!component.isValid()) {
					component = null;
					tries = 0;
				}
			}
		}
	}
}
