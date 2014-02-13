package org.powerbot.os.api.tools;

import java.awt.Color;
import java.util.Arrays;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.api.util.HashTable;
import org.powerbot.os.bot.client.Client;
import org.powerbot.os.bot.client.WidgetNode;

public class Component extends ClientAccessor {
	public static final Color TARGET_STROKE_COLOR = new Color(0, 255, 0, 150);
	public static final Color TARGET_FILL_COLOR = new Color(0, 0, 0, 50);

	private final Widget widget;
	private final Component component;
	private final int index;

	private Component[] sparseCache;

	public Component(final ClientContext ctx, final Widget widget, final int index) {
		this(ctx, widget, null, index);
	}

	public Component(final ClientContext ctx, final Widget widget, final Component component, final int index) {
		super(ctx);
		this.widget = widget;
		this.component = component;
		this.index = index;
	}

	public Widget getWidget() {
		return widget;
	}

	public int getIndex() {
		return index;
	}

	public synchronized Component getComponent(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Component c = new Component(ctx, widget, this, index);
		if (index >= sparseCache.length) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
		}
		return sparseCache[index] = c;
	}

	public synchronized int getComponentCount() {
		final org.powerbot.os.bot.client.Widget w = getInternal();
		final org.powerbot.os.bot.client.Widget[] arr = w != null ? w.getChildren() : null;
		return arr != null ? arr.length : 0;
	}

	public int getId() {
		final org.powerbot.os.bot.client.Widget w = getInternal();
		return w != null ? w.getId() : -1;
	}

	public int getParentId() {
		final Client client = ctx.client();
		final org.powerbot.os.bot.client.Widget w = getInternal();
		if (client == null || w == null) {
			return -1;
		}
		final int p = w.getParentId();
		if (p != -1) {
			return p;
		}

		final int uid = getId() >>> 16;
		for (final WidgetNode node : new HashTable<WidgetNode>(client.getWidgetTable(), WidgetNode.class)) {
			if (uid == node.getUid()) {
				return (int) node.getId();
			}
		}
		return -1;
	}

	private org.powerbot.os.bot.client.Widget getInternal() {
		final int wi = widget.getIndex();
		if (component != null) {
			final org.powerbot.os.bot.client.Widget _i = component.getInternal();
			final org.powerbot.os.bot.client.Widget[] arr = _i != null ? _i.getChildren() : null;
			if (arr != null && index < arr.length) {
				return arr[index];
			}
			return null;
		}
		final Client client = ctx.client();
		final org.powerbot.os.bot.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && wi < arr.length) {
			final org.powerbot.os.bot.client.Widget[] comps = arr[wi];
			return comps != null && index < comps.length ? comps[index] : null;
		}
		return null;
	}
}
