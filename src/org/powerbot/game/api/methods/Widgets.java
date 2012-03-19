package org.powerbot.game.api.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;

/**
 * @author Timer
 */
public class Widgets {
	private static final Map<Client, Widget[]> caches = new HashMap<Client, Widget[]>();

	public static Widget[] getLoaded() {
		final Client client = Bot.resolve().client;
		ensureCapacity(client);
		final Object[] clientInterfaceCache = client.getRSInterfaceCache();
		if (clientInterfaceCache == null) {
			return new Widget[0];
		}
		final ArrayList<Widget> validInterfaces = new ArrayList<Widget>();
		for (int index = 0; index < clientInterfaceCache.length; index++) {
			if (clientInterfaceCache[index] != null) {
				final Widget widget = get(index);
				if (widget.isValid()) {
					validInterfaces.add(widget);
				}
			}
		}
		return validInterfaces.toArray(new Widget[validInterfaces.size()]);
	}

	public static Widget get(final int index) {
		if (index < 0) {
			throw new RuntimeException("index < 0 (" + index + ")");
		}

		final Client client = Bot.resolve().client;
		Widget[] cachedInterfaces = caches.get(client);
		if (cachedInterfaces == null) {
			cachedInterfaces = new Widget[100];
			caches.put(client, cachedInterfaces);
		}
		Widget widget;
		if (index < cachedInterfaces.length) {
			widget = cachedInterfaces[index];
			if (widget == null) {
				widget = new Widget(index);
				cachedInterfaces[index] = widget;
			}
		} else {
			widget = new Widget(index);
			ensureCapacity(client);
			cachedInterfaces = caches.get(client);
			if (index < cachedInterfaces.length) {
				cachedInterfaces[index] = widget;
			}
		}
		return widget;
	}

	public static WidgetChild get(final int index, final int childIndex) {
		return get(index).getChild(childIndex);
	}

	private static void ensureCapacity(Client paramClient) {
		Object[] clientInterfaceCache = paramClient.getRSInterfaceCache();
		Widget[] cachedInterfaces = caches.get(paramClient);
		if (cachedInterfaces == null) {
			cachedInterfaces = new Widget[100];
			caches.put(paramClient, cachedInterfaces);
		}
		if ((clientInterfaceCache != null) && (cachedInterfaces.length < clientInterfaceCache.length)) {
			caches.put(paramClient, Arrays.copyOf(cachedInterfaces, clientInterfaceCache.length));
		}
	}
}
