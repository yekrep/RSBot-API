package org.powerbot.script.internal.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.util.Tracker;

@Manifest(name = "Widget closer", authors = {"Timer"}, description = "Closes widgets")
public class WidgetCloser extends PollingScript implements InternalScript {

	private static final int[] COMPONENTS = {
			21 << 16 | 43, // beholding a player's statuette (duellist's cap)
			1234 << 16 | 15, // membership offers
			906 << 16 | 378, // email register
			1252 << 16 | 8, // Squeal of Fortune notification
			1253 << 16 | 16, // Squeal of Fortune window
			1218 << 16 | 77, // advanced skill guide
			1107 << 16 | 157, // clan popups
			755 << 16 | 44, // world map
			438 << 16 | 22, //membership refer friend
	};

	private final Timer threshold;
	private volatile Component component;
	private volatile int tries;

	public WidgetCloser() {
		threshold = new Timer(0);
	}

	@Override
	public int poll() {
		if (!isValid()) {
			return -1;
		}

		Tracker.getInstance().trackPage("randoms/WidgetCloser/", "");
		getController().getLock().lock();

		if (++tries > 3) {
			threshold.setEndIn(60000);
			getController().getLock().unlock();
			return -1;
		}

		if (component.isVisible() && component.click(true)) {
			final Timer timer = new Timer(Random.nextInt(2000, 2500));
			while (timer.isRunning() && component.isVisible()) {
				sleep(100, 250);
			}
			if (!component.isVisible()) {
				component = null;
				tries = 0;
				getController().getLock().unlock();
				return -1;
			}
		}

		return -1;
	}

	public boolean isValid() {
		if (threshold.isRunning()) {
			return false;
		}
		for (final int p : COMPONENTS) {
			component = ctx.widgets.get(p >> 16, p & 0xffff);
			if (component != null && component.isVisible()) {
				break;
			} else {
				component = null;
			}
		}
		return component != null;
	}
}