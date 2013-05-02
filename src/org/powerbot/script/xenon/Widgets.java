package org.powerbot.script.xenon;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterfaceBase;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Widget;

public class Widgets {
	private static final Map<Client, Widget[]> cache = new HashMap<>();

	public static Widget get(final int widget) {
		final Client client = Bot.client();
		if (client == null || widget < 0) return null;

		Widget[] cache = Widgets.cache.get(client);
		if (cache == null) cache = new Widget[0];
		if (widget < cache.length) return cache[widget];

		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		final int mod = Math.max(containers != null ? containers.length : 0, widget + 1);
		final int len = cache.length;
		cache = Arrays.copyOf(cache, mod);
		for (int i = len; i < mod; i++) cache[i] = new Widget(i);
		Widgets.cache.put(client, cache);
		return cache[widget];
	}

	public static Component get(final int index, final int componentIndex) {
		final Widget widget = get(index);
		return widget != null ? widget.getComponent(componentIndex) : null;
	}

	public static boolean scroll(final Component component, final Component bar) {
		if (component == null || bar == null || !component.isValid() || bar.getChildrenCount() != 6) return false;
		Component area = component;
		int id;
		while (area.getScrollHeight() == 0 && (id = area.getParentId()) != -1) area = get(id >> 16, id & 0xffff);
		if (area.getScrollHeight() == 0) return false;

		final Point abs = area.getAbsoluteLocation();
		if (abs.x == -1 || abs.y == -1) return false;
		final int height = area.getHeight();
		final Point p = component.getAbsoluteLocation();
		if (p.y >= abs.y && p.y <= abs.y + height - component.getHeight()) return true;

		final Component _bar = bar.getChild(0);
		if (_bar == null) return false;
		final int size = area.getScrollHeight();
		int pos = (int) ((float) _bar.getHeight() / size * (component.getRelativeLocation().y + Random.nextInt(-height / 2, height / 2 - component.getHeight())));
		if (pos < 0) pos = 0;
		else if (pos >= _bar.getHeight()) pos = _bar.getHeight() - 1;
		final Point nav = _bar.getAbsoluteLocation();
		nav.translate(Random.nextInt(0, _bar.getWidth()), pos);
		if (!Mouse.click(nav, true)) return false;
		Delay.sleep(200, 400);

		boolean up;
		Point a;
		Component c;
		while ((a = component.getAbsoluteLocation()).y < abs.y || a.y > abs.y + height - component.getHeight()) {
			up = a.y < abs.y;
			c = bar.getChild(up ? 4 : 5);
			if (c == null) break;
			if (c.click()) Delay.sleep(100, 200);
		}
		return a.y >= abs.y && a.y <= height + abs.y + height - component.getHeight();
	}
}