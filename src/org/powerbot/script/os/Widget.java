package org.powerbot.script.os;

import java.util.Arrays;

import org.powerbot.bot.os.client.Client;

public class Widget extends ClientAccessor {
	private final int index;
	private Component[] sparseCache;

	Widget(final ClientContext ctx, final int index) {
		super(ctx);
		this.index = index;
		sparseCache = new Component[0];
	}

	public int getIndex() {
		return index;
	}

	public synchronized Component getComponent(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Component c = new Component(ctx, this, index);
		if (index >= sparseCache.length) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
		}
		return sparseCache[index] = c;
	}

	public int getComponentCount() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && index < arr.length) {
			final org.powerbot.bot.os.client.Widget[] comps = arr[index];
			return comps != null ? comps.length : 0;
		}
		return 0;
	}
}
