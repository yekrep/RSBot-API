package org.powerbot.script.os;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.client.WidgetNode;

public class Component extends ClientAccessor {
	public static final Color TARGET_STROKE_COLOR = new Color(0, 255, 0, 150);
	public static final Color TARGET_FILL_COLOR = new Color(0, 0, 0, 50);

	private final Widget widget;
	private final Component component;
	private final int index;

	private Component[] sparseCache;

	Component(final ClientContext ctx, final Widget widget, final int index) {
		this(ctx, widget, null, index);
	}

	Component(final ClientContext ctx, final Widget widget, final Component component, final int index) {
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

	public Point getScreenLocation() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Widget widget = getInternal();
		if (client == null || widget == null) {
			return new Point(-1, -1);
		}
		final int uid = getParentId();
		if (uid != -1) {
			final Component c = ctx.widgets.get(uid >> 16).getComponent(uid & 0xffff);
			final Point p = c.getScreenLocation();
			if (p.x != -1 && p.y != -1) {
				return new Point(p.x + widget.getX(), p.y + widget.getY());
			}
		}
		final int[] boundsX = client.getWidgetBoundsX(), boundsY = client.getWidgetBoundsY();
		final int bounds = getBoundsIndex();
		if (boundsX != null && boundsY != null && bounds >= 0 && bounds < boundsX.length && bounds < boundsY.length) {
			final int x = boundsX[bounds], y = boundsY[bounds];
			return new Point(x - widget.getScrollX(), y - widget.getScrollY());
		}
		return new Point(-1, -1);
	}

	public int getRelativeX() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getX() : -1;
	}

	public int getRelativeY() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getY() : -1;
	}

	public int getWidth() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getWidth() : -1;
	}

	public int getHeight() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getHeight() : -1;
	}

	public int getBorderThickness() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getBorderThickness() : -1;
	}

	public int getType() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getType() : -1;
	}

	public int getId() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getId() : -1;
	}

	public int getParentId() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Widget w = getInternal();
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
		final org.powerbot.bot.os.client.Widget w = getInternal();
		final org.powerbot.bot.os.client.Widget[] arr = w != null ? w.getChildren() : null;
		return arr != null ? arr.length : 0;
	}

	public int getContentType() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getContentType() : -1;
	}

	public int getModelId() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getModelId() : -1;
	}

	public int getModelType() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getModelType() : -1;
	}

	public int getModelZoom() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getModelZoom() : -1;
	}

	public String[] getActions() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		final String[] arr = w != null ? w.getActions() : new String[0];
		for (int i = 0; i < (arr != null ? arr.length : 0); i++) {
			if (arr[i] == null) {
				arr[i] = "";
			}
		}
		return arr != null ? arr : new String[0];
	}

	public int getAngleX() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getAngleX() : -1;
	}

	public int getAngleY() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getAngleY() : -1;
	}

	public int getAngleZ() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getAngleZ() : -1;
	}

	public String getText() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		final String str = w != null ? w.getText() : "";
		return str != null ? str : "";
	}

	public int getTextColor() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getTextColor() : -1;
	}

	public int getScrollX() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getScrollX() : -1;
	}

	public int getScrollY() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getScrollY() : -1;
	}

	public int getScrollWidth() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getScrollWidth() : -1;
	}

	public int getScrollHeight() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getScrollHeight() : -1;
	}

	public int getBoundsIndex() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getBoundsIndex() : -1;
	}

	public int getTextureId() {
		final org.powerbot.bot.os.client.Widget w = getInternal();
		return w != null ? w.getTextureId() : -1;
	}

	private org.powerbot.bot.os.client.Widget getInternal() {
		final int wi = widget.getIndex();
		if (component != null) {
			final org.powerbot.bot.os.client.Widget _i = component.getInternal();
			final org.powerbot.bot.os.client.Widget[] arr = _i != null ? _i.getChildren() : null;
			if (arr != null && index < arr.length) {
				return arr[index];
			}
			return null;
		}
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && wi < arr.length) {
			final org.powerbot.bot.os.client.Widget[] comps = arr[wi];
			return comps != null && index < comps.length ? comps[index] : null;
		}
		return null;
	}
}
