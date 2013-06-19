package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.RSInterfaceBase;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

import java.awt.Point;
import java.util.Arrays;

/**
 * {@link Widgets} is a static utility which provides access to the game's {@link Component}s by means of {@link Widget}s.
 * <p/>
 * {@link Widget}s are cached and are available at all times, even when not present in game.
 * {@link Widget}s must be validated before use.
 */
public class Widgets extends MethodProvider {
	public Widget[] cache;

	public Widgets(MethodContext factory) {
		super(factory);
	}

	/**
	 * Returns all the {@link Widget}s that are currently loaded in the game.
	 *
	 * @return an array of {@link Widget}s which are currently loaded
	 */
	public Widget[] getLoaded() {
		Client client = ctx.getClient();
		if (client == null) {
			return new Widget[0];
		}
		final RSInterfaceBase[] containers = client.getRSInterfaceCache().clone();
		final int len = containers != null ? containers.length : 0;
		final Widget[] arr = new Widget[len];
		for (int i = 0; i < len; i++) {
			arr[i] = new Widget(ctx, i);
		}
		return arr;
	}

	/**
	 * Retrieves the cached {@link Widget} for the given index.
	 *
	 * @param widget the index of the desired {@link Widget}
	 * @return the {@link Widget} respective to the given index
	 */
	public synchronized Widget get(final int widget) {
		Client client = ctx.getClient();
		if (widget < 0) {
			return null;
		}

		if (cache == null) {
			cache = new Widget[0];
		}
		if (widget < cache.length) {
			return cache[widget];
		}

		final RSInterfaceBase[] containers = client != null ? client.getRSInterfaceCache() : new RSInterfaceBase[0];
		final int mod = Math.max(containers != null ? containers.length : 0, widget + 1);
		final int len = cache.length;
		cache = Arrays.copyOf(cache, mod);
		for (int i = len; i < mod; i++) {
			cache[i] = new Widget(ctx, i);
		}
		return cache[widget];
	}

	/**
	 * Retrieves the cached {@link Component} of the given {@link Widget} index.
	 *
	 * @param index          the index of the desired {@link Widget}
	 * @param componentIndex the index of the desired {@link Component} of the given {@link Widget}
	 * @return the {@link Component} belonging to the {@link Widget} requested
	 */
	public Component get(final int index, final int componentIndex) {
		return get(index).getComponent(componentIndex);
	}

	/**
	 * Scrolls a component into view.
	 *
	 * @param component the {@link Component} which is desired to be visible
	 * @param bar       the {@link Component} of the scroll bar
	 * @return {@code true} if visible; otherwise {@code false}
	 */
	public boolean scroll(Component component, Component bar, boolean scroll) {
		if (component == null || !component.isValid()) return false;
		if (bar == null || !bar.isValid() || bar.getChildrenCount() != 6) return false;
		Component pane = component;
		int id;
		while (pane.getMaxVerticalScroll() == 0 && (id = pane.getParentId()) != -1) {
			pane = ctx.widgets.get(id >> 16, id & 0xffff);
		}
		if (pane.getMaxVerticalScroll() == 0) return false;
		return scroll(component, pane, bar, scroll);
	}

	public boolean scroll(Component component, Component pane, Component bar, boolean scroll) {
		if (component == null || !component.isValid()) return false;
		if (bar == null || !bar.isValid() || bar.getChildrenCount() != 6) return false;
		if (pane == null || !pane.isValid() || pane.getScrollHeight() == 0) return false;
		Point view = pane.getAbsoluteLocation();
		int height = pane.getScrollHeight();
		if (view.x < 0 || view.y < 0 || height < 1) return false;
		Point pos = component.getAbsoluteLocation();
		int length = component.getHeight();
		if (pos.y >= view.y && pos.y <= view.y + height - length) return true;
		Component thumbHolder = bar.getChild(0);
		Component thumb = bar.getChild(1);
		int thumbSize = thumbHolder.getScrollHeight();
		int y = (int) ((float) thumbSize / pane.getMaxVerticalScroll() *
				(component.getRelativeLocation().y + Random.nextInt(-height / 2, height / 2 - length)));
		if (y < 0) {
			y = 0;
		} else if (y >= thumbSize) y = thumbSize - 1;
		Point p = thumbHolder.getAbsoluteLocation();
		p.translate(Random.nextInt(0, thumbHolder.getWidth()), y);
		if (!scroll) {
			if (!ctx.mouse.click(p, true)) return false;
			sleep(200, 400);
		}
		Point a;
		Component c;
		int tY = thumb.getAbsoluteLocation().y;
		long mark = System.currentTimeMillis();
		int scrolls = 0;
		while ((a = component.getAbsoluteLocation()).y < view.y || a.y > view.y + height - length) {
			if (scroll) {
				if (ctx.mouse.scroll(a.y > view.y)) {
					if (++scrolls >= Random.nextInt(5, 9)) {
						sleep(200, 700);
						scrolls = 0;
					}
					sleep(25, 100);
					if (System.currentTimeMillis() - mark > 2000) {
						int l = thumb.getAbsoluteLocation().y;
						if (tY == l) {
							return scroll(component, pane, bar, false);
						} else {
							mark = System.currentTimeMillis();
							tY = l;
						}
					}
				} else {
					break;
				}
			} else {
				c = bar.getChild(a.y < view.y ? 4 : 5);
				if (c == null) {
					break;
				}
				if (c.click()) sleep(50, 300);
			}
		}
		return a.y >= view.y && a.y <= height + view.y + height - length;
	}
}