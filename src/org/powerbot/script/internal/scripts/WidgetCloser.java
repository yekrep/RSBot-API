package org.powerbot.script.internal.scripts;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;

/**
 * @author Timer
 */
public class WidgetCloser extends PollingScript implements InternalScript {
	private static final int[] COMPONENTS = {
			1252 << 16 | 8, // Squeal of Fortune notification
			1253 << 16 | 36, // Squeal of Fortune window
			906 << 16 | 302, // validate email
			1139 << 16 | 12, // Extras window
			438 << 16 | 24,//recruit a friend
			622 << 16 | 21,//member loyalty
			204 << 16 | 3,//membership offer
	};

	private Component component;
	private int tries;

	public WidgetCloser() {
		priority.set(-1);
	}

	@Override
	public int poll() {
		if (!isValid()) {
			return -1;
		}

		Component component = this.component;
		if (component == null) {
			return -1;
		}

		if (++tries > 3) {
			tries = 0;
			return Random.nextInt(30, 61) * 1000;
		}

		if (component.click(true)) {
			final Timer timer = new Timer(Random.nextInt(2000, 2500));
			while (timer.isRunning() && component.isVisible()) {
				sleep(175);
			}
			if (!component.isVisible()) {
				tries = 0;
			}
		}

		return -1;
	}

	private boolean isValid() {
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
