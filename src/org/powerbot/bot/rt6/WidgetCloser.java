package org.powerbot.bot.rt6;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
			1253 << 16 | 176, // Squeal of Fortune window
			906 << 16 | 302, // validate email
			1139 << 16 | 12, // Extras window
			438 << 16 | 24,//recruit a friend
			622 << 16 | 21,//member loyalty
			204 << 16 | 3,//membership offer
			149 << 16 | 237,//pickaxe
			1252 << 16 | 6, // Squeal of Fortune notification
			1223 << 16 | 18,//Achievement continue button
	};

	private volatile Component component;
	private final AtomicLong time;
	private final AtomicInteger tries;

	public WidgetCloser() {
		priority.set(5);
		time = new AtomicLong(0L);
		tries = new AtomicInteger();
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

		if (tries.incrementAndGet() >= 3) {
			time.set(System.nanoTime() + TimeUnit.NANOSECONDS.toSeconds(Random.nextInt(30, 61)));
			tries.set(0);
			return;
		}

		if (component.click(true)) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !component.visible();
				}
			}, 175)) {
				tries.set(0);
			}
		}
	}

	private boolean isValid() {
		if (ctx.properties.getProperty("widget.closer.disable", "").equals("true")) {
			return false;
		}
		if (System.nanoTime() < time.get() || ctx.bank.opened()) {
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
