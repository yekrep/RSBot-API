package org.powerbot.bot.script.daemon;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.rs3.tools.Component;

/**
 */
public class WidgetCloser extends PollingScript implements InternalScript {
	private static final int[] COMPONENTS = {
			906 << 16 | 545,//transaction
			335 << 16 | 3,//trade window
			1422 << 16 | 18, //world map
			1253 << 16 | 36, // Squeal of Fortune window
			906 << 16 | 302, // validate email
			1139 << 16 | 12, // Extras window
			438 << 16 | 24,//recruit a friend
			622 << 16 | 21,//member loyalty
			204 << 16 | 3,//membership offer
			149 << 16 | 237,//pickaxe
			1252 << 16 | 8, // Squeal of Fortune notification
	};

	private Component component;
	private long time;
	private int tries;

	public WidgetCloser() {
		priority.set(5);
		time = 0;
	}

	@Override
	public int poll() {
		if (!isValid()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());

		final Component component = this.component;
		if (component == null) {
			return 0;
		}

		if (++tries >= 3) {
			time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(Random.nextInt(30, 61), TimeUnit.SECONDS);
			tries = 0;
			return 0;
		}

		if (component.click(true)) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !component.isVisible();
				}
			}, 175)) {
				tries = 0;
			}
		}

		return -1;
	}

	private boolean isValid() {
		if (System.nanoTime() < time || ctx.bank.isOpen()) {
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
