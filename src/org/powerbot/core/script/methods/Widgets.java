package org.powerbot.core.script.methods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.core.Bot;
import org.powerbot.core.script.wrappers.Widget;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterfaceBase;

public class Widgets {
	private static final Map<Client, Widget.Container[]> cache = new HashMap<>();

	public static Widget.Container get(final int index) {
		final Client client = Bot.client();
		if (client == null || index < 0) return null;

		Widget.Container[] cache = Widgets.cache.get(client);
		if (index < cache.length) return cache[index];

		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		final int mod = Math.max(containers != null ? containers.length : 0, index + 1);
		final int len = cache.length;
		cache = Arrays.copyOf(cache, mod);
		for (int i = len; i < mod; i++) cache[i] = new Widget.Container(i);
		Widgets.cache.put(client, cache);
		return cache[index];
	}

	public static Widget get(final int containerId, final int index) {
		final Widget.Container container = get(containerId);
		return container.getWidget(index);
	}
}
