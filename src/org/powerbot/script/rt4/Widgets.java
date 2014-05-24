package org.powerbot.script.rt4;

import java.util.Arrays;

import org.powerbot.bot.rt4.client.Client;

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

	public Component component(final int index, final int componentIndex) {
		return widget(index).component(componentIndex);
	}
}
