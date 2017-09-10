package org.powerbot.bot.rt4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Constants;

public class WidgetCloser extends PollingScript<ClientContext> {
	private final Map<Integer, AtomicInteger> attempts;

	public WidgetCloser() {
		priority.set(5);
		attempts = new HashMap<Integer, AtomicInteger>();
		for (final int i : Constants.WIDGETCLOSER_TRADE_ITEMS) {
			attempts.put(i, new AtomicInteger(0));
		}
	}

	@Override
	public void poll() {
		final List<Integer> w = new ArrayList<Integer>();

		if (!ctx.bot().allowTrades()) {
			for (final int e : Constants.WIDGETCLOSER_TRADE_ITEMS) {
				w.add(e);
			}
		}

		for (final int id : w) {
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
	}
}
