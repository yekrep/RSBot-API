package org.powerbot.bot.rt6;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;

public class WidgetCloser extends PollingScript<ClientContext> {
	private static final int[] COMPONENTS = {
			906 << 16 | 545,//transaction
			335 << 16 | 3,//trade window
			1422 << 16 | 18, //world map
			1253 << 16 | 176, // Squeal of Fortune window
			906 << 16 | 517, // validate email
			1139 << 16 | 12, // Extras window
			438 << 16 | 24,//recruit a friend
			622 << 16 | 21,//member loyalty
			204 << 16 | 3,//membership offer
			149 << 16 | 237,//pickaxe
			1252 << 16 | 6, // Squeal of Fortune notification
			1223 << 16 | 18,//Achievement continue button
			1048 << 16 | 30, // key tokens
	};
	private static final int[] COMPONENTS_DIE = {
			906 << 16 | 476, // change email
	};
	private final Map<Integer, AtomicInteger> attempts;

	public WidgetCloser() {
		priority.set(5);
		attempts = new HashMap<Integer, AtomicInteger>();
		for (final int i : COMPONENTS) {
			attempts.put(i, new AtomicInteger(0));
		}
	}

	@Override
	public void poll() {
		if (ctx.properties.getProperty("widget.closer.disable", "").equals("true") || ctx.bank.opened()) {
			return;
		}

		for (final int id : COMPONENTS) {
			final AtomicInteger a = attempts.get(id);
			if (a.get() >= 3) {
				continue;
			}

			final Component c = ctx.widgets.component(id >> 16, id & 0xffff);
			final Point p = c.screenPoint();
			if (c.visible() && c.click()) {
				if (Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return !c.visible() || !c.screenPoint().equals(p);
					}
				})) {
					a.set(0);
				} else {
					a.incrementAndGet();
				}
			}
		}

		for (final int id : COMPONENTS_DIE) {
			if (ctx.widgets.component(id >> 16, id & 0xffff).visible()) {
				ctx.controller.stop();
				return;
			}
		}
	}
}
