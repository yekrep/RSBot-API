package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.RSInterfaceBase;
import org.powerbot.script.util.Delay;
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
			return null;
		}
		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
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
	public Widget get(final int widget) {
		Client client = ctx.getClient();
		if (client == null || widget < 0) {
			return null;
		}

		if (cache == null) {
			cache = new Widget[0];
		}
		if (widget < cache.length) {
			return cache[widget];
		}

		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
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
		final Widget widget = get(index);
		return widget != null ? widget.getComponent(componentIndex) : null;
	}

	/**
	 * Scrolls a component into view.
	 *
	 * @param component the {@link Component} which is desired to be visible
	 * @param bar       the {@link Component} of the scroll bar
	 * @return {@code true} if visible; otherwise {@code false}
	 */
	public boolean scroll(final Component component, final Component bar) {
		if (component == null || bar == null || !component.isValid() || bar.getChildrenCount() != 6) {
			return false;
		}
		Component area = component;
		int id;
		while (area.getScrollHeight() == 0 && (id = area.getParentId()) != -1) {
			area = get(id >> 16, id & 0xffff);
		}
		if (area.getScrollHeight() == 0) {
			return false;
		}

		final Point abs = area.getAbsoluteLocation();
		if (abs.x == -1 || abs.y == -1) {
			return false;
		}
		final int height = area.getHeight();
		final Point p = component.getAbsoluteLocation();
		if (p.y >= abs.y && p.y <= abs.y + height - component.getHeight()) {
			return true;
		}

		final Component _bar = bar.getChild(0);
		if (_bar == null) {
			return false;
		}
		final int size = area.getScrollHeight();
		int pos = (int) ((float) _bar.getHeight() / size * (component.getRelativeLocation().y + Random.nextInt(-height / 2, height / 2 - component.getHeight())));
		if (pos < 0) {
			pos = 0;
		} else if (pos >= _bar.getHeight()) {
			pos = _bar.getHeight() - 1;
		}
		final Point nav = _bar.getAbsoluteLocation();
		nav.translate(Random.nextInt(0, _bar.getWidth()), pos);
		if (!ctx.mouse.click(nav, true)) {
			return false;
		}
		Delay.sleep(200, 400);

		boolean up;
		Point a;
		Component c;
		while ((a = component.getAbsoluteLocation()).y < abs.y || a.y > abs.y + height - component.getHeight()) {
			up = a.y < abs.y;
			c = bar.getChild(up ? 4 : 5);
			if (c == null) {
				break;
			}
			if (c.click()) {
				Delay.sleep(100, 200);
			}
		}
		return a.y >= abs.y && a.y <= height + abs.y + height - component.getHeight();
	}
}