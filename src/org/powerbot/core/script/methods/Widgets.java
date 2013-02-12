package org.powerbot.core.script.methods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.core.Bot;
import org.powerbot.core.script.wrappers.Component;
import org.powerbot.core.script.wrappers.Widget;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterfaceBase;

public class Widgets {
	private static final Map<Client, Widget[]> cache = new HashMap<>();

	public static Widget get(final int widget) {
		final Client client = Bot.client();
		if (client == null || widget < 0) return null;

		Widget[] cache = Widgets.cache.get(client);
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
}
