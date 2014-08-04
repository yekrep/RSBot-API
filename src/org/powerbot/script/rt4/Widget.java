package org.powerbot.script.rt4;

import java.util.Arrays;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Validatable;

public class Widget extends ClientAccessor implements Identifiable, Validatable {
	private final int index;
	private Component[] sparseCache;

	Widget(final ClientContext ctx, final int index) {
		super(ctx);
		this.index = index;
		sparseCache = new Component[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int id() {
		return index;
	}

	public synchronized Component component(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Component c = new Component(ctx, this, index);
		final int l = sparseCache.length;
		if (index >= l) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
			for (int i = l; i < index + 1; i++) {
				sparseCache[i] = new Component(ctx, this, i);
			}
		}
		return sparseCache[index] = c;
	}

	public int componentCount() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && index < arr.length) {
			final org.powerbot.bot.rt4.client.Widget[] comps = arr[index];
			return comps != null ? comps.length : 0;
		}
		return 0;
	}

	public Component[] components() {
		final int len = componentCount();
		if (len <= 0) {
			return new Component[0];
		}
		component(len - 1);
		return Arrays.copyOf(sparseCache, len);
	}

	@Override
	public boolean valid() {
		if (index < 0) {
			return false;
		}
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		return arr != null && index > -1 && index < arr.length;
	}
}
