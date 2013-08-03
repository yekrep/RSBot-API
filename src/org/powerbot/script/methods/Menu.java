package org.powerbot.script.methods;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.powerbot.client.Client;
import org.powerbot.client.MenuGroupNode;
import org.powerbot.client.MenuItemNode;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeSubQueue;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.internal.wrappers.Queue;
import org.powerbot.script.util.Random;
import org.powerbot.util.StringUtil;

public class Menu extends MethodProvider {
	public Menu(MethodContext factory) {
		super(factory);
	}

	public boolean isOpen() {
		Client client = ctx.getClient();
		return client != null && client.isMenuOpen();
	}

	public int indexOf(final String action) {
		return indexOf(action, null);
	}

	public int indexOf(String action, String option) {
		List<MenuItemNode> nodes = getMenuItemNodes();
		int d = 0;
		for (MenuItemNode node : nodes) {
			String a = node.getAction(), o = node.getOption();
			a = a != null ? StringUtil.stripHtml(a).toLowerCase() : "";
			o = o != null ? StringUtil.stripHtml(o).toLowerCase() : "";
			if ((action == null || a.contains(action.toLowerCase())) &&
					(option == null || o.contains(option.toLowerCase()))) {
				return d;
			}
			d++;
		}
		return -1;
	}

	public boolean hover(final String action) {
		return hover(action, null);
	}

	public boolean hover(final String action, final String option) {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		int index = indexOf(action, option);
		if (index == -1) {
			return false;
		}
		if (!client.isMenuOpen()) {
			if (ctx.mouse.click(false)) {
				final long m = System.currentTimeMillis();
				while (System.currentTimeMillis() - m < 100 && !client.isMenuOpen()) {
					sleep(5);
				}
				sleep(0, 300);

				if (!client.isMenuOpen()) {
					return false;
				}
				if ((index = indexOf(action, option)) == -1) {
					close();
					return false;
				}
			}
		}
		return hoverIndex(client, index);
	}

	public boolean click(final String action) {
		return click(action, null);
	}

	public boolean click(final String action, final String option) {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		int index = indexOf(action, option);
		if (index == -1) {
			return false;
		}
		if (!client.isMenuOpen() && index == 0) {
			return ctx.mouse.click(true);
		}
		return hover(action, option) && ctx.mouse.click(true);
	}

	public boolean close() {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		if (client.isMenuOpen()) {
			ctx.mouse.move(client.getMenuX() - 30 + Random.nextInt(0, 20), client.getMenuY() - 10 + Random.nextInt(0, 20));
		}
		return !client.isMenuOpen();
	}

	private boolean hoverIndex(final Client client, int index) {
		int _index = 0, main = 0;
		final NodeSubQueue menu;
		collapsed:
		if (client.isMenuCollapsed()) {
			if ((menu = client.getCollapsedMenuItems()) != null) {
				final Queue<MenuGroupNode> groups = new Queue<>(menu);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext(), ++main) {
					int sub = 0;
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<>(queue);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext(), ++sub) {
						if (_index++ == index) {
							if (sub == 0) {
								break collapsed;
							} else {
								return hoverSub(client, main, sub);
							}
						}
					}
				}
			}
			if (client.isMenuOpen()) {
				close();
			}
			return false;
		}
		return ctx.mouse.move(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * index + Random.nextInt(3, 12))
		) && client.isMenuOpen();
	}

	private boolean hoverSub(final Client client, final int main, final int sub) {
		if (ctx.mouse.move(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * main + Random.nextInt(3, 12)))) {
			sleep(Random.nextInt(125, 175));
			if (client.isMenuOpen()) {
				final Point p = ctx.mouse.getLocation();
				final int cX;
				final int subX = client.getSubMenuX();
				if (ctx.mouse.move(cX = subX + Random.nextInt(4, client.getSubMenuWidth() - 5), p.y)) {
					sleep(Random.nextInt(125, 175));
					if (client.isMenuOpen()) {
						final int subY = client.getSubMenuY();
						if (ctx.mouse.move(cX, subY + (16 * sub + Random.nextInt(3, 12) + 21))) {
							sleep(Random.nextInt(125, 175));
							return client.isMenuOpen();
						}
					}
				}
			}
		}
		if (client.isMenuOpen()) {
			close();
		}
		return false;
	}

	private List<MenuItemNode> getMenuItemNodes() {
		final List<MenuItemNode> nodes = new LinkedList<>();

		Client client = ctx.getClient();
		if (client == null) {
			return nodes;
		}

		final boolean collapsed;
		if (collapsed = client.isMenuCollapsed()) {
			final NodeSubQueue menu = client.getCollapsedMenuItems();
			if (menu != null) {
				final Queue<MenuGroupNode> groups = new Queue<>(menu);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext()) {
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<>(queue);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext()) {
						nodes.add(node);
					}
				}
			}
		} else {
			final NodeDeque menu = client.getMenuItems();
			if (menu != null) {
				final Deque<MenuItemNode> deque = new Deque<>(menu);
				for (MenuItemNode node = deque.getHead(); node != null; node = deque.getNext()) {
					nodes.add(node);
				}
			}
		}
		if (nodes.size() > 1) {
			final MenuItemNode node = nodes.get(0);
			final String action = node.getAction();
			if (action != null && StringUtil.stripHtml(action).equalsIgnoreCase(collapsed ? "Walk here" : "Cancel")) {
				Collections.reverse(nodes);
			}
		}
		return nodes;
	}

	public String[] getItems() {
		List<MenuItemNode> nodes = getMenuItemNodes();
		int len = nodes.size();
		int d = 0;
		String[] arr = new String[len];
		for (MenuItemNode node : nodes) {
			String a = node.getAction(), o = node.getOption();
			if (a != null) {
				a = StringUtil.stripHtml(a);
			}
			if (o != null) {
				o = StringUtil.stripHtml(o);
			}
			arr[d++] = a + " " + o;
		}
		return arr;
	}
}
