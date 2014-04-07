package org.powerbot.bot.rt6.activation;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;

public class WidgetCloser extends PollingScript<ClientContext> {
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
	public void poll() {
		if (!isValid()) {
			if (threshold.contains(this)) {
				threshold.remove(this);
			}
			return;
		}
		if (!threshold.contains(this)) {
			threshold.add(this);
		}

		final Component component = this.component;
		if (component == null) {
			return;
		}

		if (++tries >= 3) {
			time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(Random.nextInt(30, 61), TimeUnit.SECONDS);
			tries = 0;
			return;
		}

		if (component.click(true)) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !component.visible();
				}
			}, 175)) {
				tries = 0;
			}
		}
	}

	private boolean isValid() {
		if (System.nanoTime() < time || ctx.bank.opened()) {
			return false;
		}

		for (final int p : COMPONENTS) {
			component = ctx.widgets.component(p >> 16, p & 0xffff);
			if (component != null && component.visible()) {
				break;
			} else {
				component = null;
			}
		}
		return component != null;
	}
}
