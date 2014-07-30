package org.powerbot.bot.rt6;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Constants;

public class WidgetCloser extends PollingScript<ClientContext> {
	private final Map<Integer, AtomicInteger> attempts;

	public WidgetCloser() {
		priority.set(5);
		attempts = new HashMap<Integer, AtomicInteger>();
		for (final int i : Constants.WIDGETCLOSER_COMPONENTS) {
			attempts.put(i, new AtomicInteger(0));
		}
	}

	@Override
	public void poll() {
		if (ctx.properties.getProperty("widget.closer.disable", "").equals("true")) {
			return;
		}

		for (final int id : ctx.bank.opened() ? Constants.WIDGETCLOSER_COMPONENTS_ACTIVE : Constants.WIDGETCLOSER_COMPONENTS) {
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

		for (final int id : Constants.WIDGETCLOSER_COMPONENTS_DIE) {
			if (ctx.widgets.component(id >> 16, id & 0xffff).visible()) {
				ctx.controller.stop();
				return;
			}
		}
	}
}
