package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Random;

public class Widgets extends ClientAccessor {
	private Widget[] sparseCache;

	public Widgets(final ClientContext ctx) {
		super(ctx);
		sparseCache = new Widget[0];
	}

	public synchronized Widget widget(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Widget c = new Widget(ctx, index);
		final int l = sparseCache.length;
		if (index >= l) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
			for (int i = l; i < index + 1; i++) {
				sparseCache[i] = new Widget(ctx, i);
			}
		}
		return sparseCache[index] = c;
	}

	public Widget[] array() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget[][] a = client != null ? client.getWidgets() : null;
		final int len = a != null ? a.length : 0;
		if (len <= 0) {
			return new Widget[0];
		}
		widget(len - 1);
		return Arrays.copyOf(sparseCache, len);
	}

	public boolean scroll(final Component container, final Component component, final Component bar) {
		final Rectangle rect_d = container.boundingRect();
		if (rect_d.contains(component.boundingRect())) {
			return true;
		}
		final Point p = rect_d.getLocation();
		p.translate(Random.nextInt(10, rect_d.width - 10), Random.nextInt(10, rect_d.height - 10));
		if (!ctx.input.move(p)) {
			return false;
		}
		Rectangle r;
		while (!rect_d.contains(r = component.boundingRect())) {
			ctx.input.scroll(r.y > rect_d.y);
		}
		return false;
	}
}
