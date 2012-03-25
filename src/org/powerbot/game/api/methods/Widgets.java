package org.powerbot.game.api.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;

/**
 * A utility for the manipulation of widgets.
 *
 * @author Timer
 */
public class Widgets {
	private static final Map<Client, Widget[]> caches = new HashMap<Client, Widget[]>();

	/**
	 * @return An <code>Widget[]</code> of the latest cached widgets.
	 */
	public static Widget[] getLoaded() {
		final Client client = Bot.resolve().getClient();
		if (client == null) {
			return new Widget[0];
		}
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

	/**
	 * @param index The position in the widget cache.
	 * @return The <code>Widget</code> retrieved from the cache.
	 */
	public static Widget get(final int index) {
		if (index < 0) {
			throw new RuntimeException("index < 0 (" + index + ")");
		}

		final Client client = Bot.resolve().getClient();
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

	/**
	 * @param index      The position in the widget cache.
	 * @param childIndex The position of the widget child in the children array of the cached widget.
	 * @return The <code>WidgetChild</code> retrieved from the children of the cached widget.
	 */
	public static WidgetChild get(final int index, final int childIndex) {
		return get(index).getChild(childIndex);
	}

	public static WidgetChild getChild(final int id) {
		final int x = id >> 16;
		final int y = id & 0xFFFF;
		return get(x).getChild(y);
	}

	/**
	 * @param client The <code>Client</code> to ensure caching capacity of.
	 */
	private static void ensureCapacity(final Client client) {
		if (client == null) {
			return;
		}
		final Object[] clientInterfaceCache = client.getRSInterfaceCache();
		Widget[] cachedInterfaces = caches.get(client);
		if (cachedInterfaces == null) {
			cachedInterfaces = new Widget[100];
			caches.put(client, cachedInterfaces);
		}
		if ((clientInterfaceCache != null) && (cachedInterfaces.length < clientInterfaceCache.length)) {
			caches.put(client, Arrays.copyOf(cachedInterfaces, clientInterfaceCache.length));
		}
	}
}
