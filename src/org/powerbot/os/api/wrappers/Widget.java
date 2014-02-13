package org.powerbot.os.api.wrappers;

import java.util.Arrays;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.client.Client;

public class Widget extends ClientAccessor {
	private final int index;
	private Component[] sparseCache;

	public Widget(final ClientContext ctx, final int index) {
		super(ctx);
		this.index = index;
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
		final org.powerbot.os.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && index < arr.length) {
			final org.powerbot.os.client.Widget[] comps = arr[index];
			return comps != null ? comps.length : 0;
		}
		return 0;
	}
}
