package org.powerbot.script.xenon;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.client.MenuGroupNode;
import org.powerbot.client.MenuItemNode;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeSubQueue;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.internal.wrappers.Queue;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.util.StringUtil;

public class Menu {
	public static boolean isOpen() {
		final Client client = Bot.client();
		return client != null && client.isMenuOpen();
	}

	public static int indexOf(final String action) {
		return indexOf(action, null);
	}

	public static int indexOf(String action, String option) {
		final List<MenuItemNode> nodes = getMenuItemNodes();
		int d = 0;
		for (final MenuItemNode node : nodes) {
			String a = node.getAction(), o = node.getOption();
			a = a != null ? StringUtil.stripHtml(a).toLowerCase() : "";
			o = o != null ? StringUtil.stripHtml(o).toLowerCase() : "";
			if ((action == null || a.contains(action.toLowerCase())) &&
					(option == null || o.contains(option.toLowerCase()))) return d;
			d++;
		}
		return -1;
	}

	public static boolean click(final String action) {
		return click(action, null);
	}

	public static boolean click(final String action, final String option) {
		final Client client = Bot.client();
		if (client == null) return false;
		int index = indexOf(action, option);
		if (index == -1) return false;
		if (!client.isMenuOpen()) {
			if (index == 0) return Mouse.click(true);
			if (Mouse.click(false)) {
				final long m = System.currentTimeMillis();
				while (System.currentTimeMillis() - m < 100 && !client.isMenuOpen()) {
					Delay.sleep(5);
				}
				Delay.sleep(0, 300);

				if (!client.isMenuOpen()) return false;
				if ((index = indexOf(action, option)) == -1) {
					close();
					return false;
				}
			}
		}
		return clickIndex(client, index);
	}

	public static boolean close() {
		final Client client = Bot.client();
		if (client == null) return false;
		if (client.isMenuOpen()) {
			Mouse.move(client.getMenuX() - 30 + Random.nextInt(0, 20), client.getMenuY() - 10 + Random.nextInt(0, 20));
		}
		return !client.isMenuOpen();
	}

	private static boolean clickIndex(final Client client, int index) {
		int _index = 0, main = 0;
		final NodeSubQueue menu;
		collapsed:
		if (client.isMenuCollapsed()) {
			if ((menu = client.getCollapsedMenuItems()) != null) {
				final Queue<MenuGroupNode> groups = new Queue<>(menu);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext(), ++main) {
					int sub = 0;
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) continue;
					final Queue<MenuItemNode> queue2 = new Queue<>(queue);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext(), ++sub) {
						if (_index++ == index) if (sub == 0) break collapsed;
						else return clickSub(client, main, sub);
					}
				}
			}
			if (client.isMenuOpen()) close();
			return false;
		}
		return Mouse.move(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * index + Random.nextInt(3, 12))
		) && client.isMenuOpen() && Mouse.click(true);
	}

	private static boolean clickSub(final Client client, final int main, final int sub) {
		if (Mouse.move(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * main + Random.nextInt(3, 12)))) {
			Delay.sleep(Random.nextInt(125, 175));
			if (client.isMenuOpen()) {
				final Point p = Mouse.getLocation();
				final int cX;
				final int subX = client.getSubMenuX();
				if (Mouse.move(cX = subX + Random.nextInt(4, client.getSubMenuWidth() - 5), p.y)) {
					Delay.sleep(Random.nextInt(125, 175));
					if (client.isMenuOpen()) {
						final int subY = client.getSubMenuY();
						if (Mouse.move(cX, subY + (16 * sub + Random.nextInt(3, 12) + 21))) {
							Delay.sleep(Random.nextInt(125, 175));
							return client.isMenuOpen() && Mouse.click(true);
						}
					}
				}
			}
		}
		if (client.isMenuOpen()) close();
		return false;
	}

	private static List<MenuItemNode> getMenuItemNodes() {
		final List<MenuItemNode> nodes = new LinkedList<>();

		final Client client = Bot.client();
		if (client == null) return nodes;

		final boolean collapsed;
		if (collapsed = client.isMenuCollapsed()) {
			final NodeSubQueue menu = client.getCollapsedMenuItems();
			if (menu != null) {
				final Queue<MenuGroupNode> groups = new Queue<>(menu);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext()) {
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) continue;
					final Queue<MenuItemNode> queue2 = new Queue<>(queue);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext()) nodes.add(node);
				}
			}
		} else {
			final NodeDeque menu = client.getMenuItems();
			if (menu != null) {
				final Deque<MenuItemNode> deque = new Deque<>(menu);
				for (MenuItemNode node = deque.getHead(); node != null; node = deque.getNext()) nodes.add(node);
			}
		}
		if (nodes.size() > 1) {
			final MenuItemNode node = nodes.get(0);
			final String action = node.getAction();
			if (action != null && StringUtil.stripHtml(action).equalsIgnoreCase(collapsed ? "Walk here" : "Cancel"))
				Collections.reverse(nodes);
		}
		return nodes;
	}

	public static String[] getItems() {
		final List<MenuItemNode> nodes = getMenuItemNodes();
		final int len = nodes.size();
		int d = 0;
		final String[] arr = new String[len];
		for (final MenuItemNode node : nodes) {
			String a = node.getAction(), o = node.getOption();
			if (a != null) a = StringUtil.stripHtml(a);
			if (o != null) o = StringUtil.stripHtml(o);
			arr[d++] = a + " " + o;
		}
		return arr;
	}
}
