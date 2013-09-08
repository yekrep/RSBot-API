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
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.util.StringUtil;

/**
 * Utilities pertaining to the in-game menu.
 *
 * @author Timer
 */
public class Menu extends MethodProvider {
	public Menu(MethodContext factory) {
		super(factory);
	}

	public static class Entry {
		public final String action, option;

		private Entry(MenuItemNode node) {
			String a = node.getAction(), o = node.getOption();
			this.action = a != null ? StringUtil.stripHtml(a) : "";
			this.option = o != null ? StringUtil.stripHtml(o) : "";
		}
	}

	/**
	 * Creates a filter obeying the provided action.
	 *
	 * @param action the action to filter
	 * @return the filter
	 */
	public static Filter<Entry> filter(String action) {
		return filter(action, null);
	}

	/**
	 * Creates a filter obeying the provided action and option.
	 *
	 * @param action the action to filter
	 * @param option the option to filter
	 * @return the filter
	 */
	public static Filter<Entry> filter(String action, String option) {
		final String a = action != null ? action.toLowerCase() : null;
		final String o = option != null ? option.toLowerCase() : null;
		return new Filter<Entry>() {
			@Override
			public boolean accept(Entry entry) {
				return (a == null || entry.action.toLowerCase().contains(a)) &&
						(o == null || entry.option.toLowerCase().contains(o));
			}
		};
	}

	/**
	 * Determines if the menu is open.
	 *
	 * @return <tt>true</tt> if the menu is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen() {
		Client client = ctx.getClient();
		return client != null && client.isMenuOpen();
	}

	/**
	 * Determines the first index of a specified filter.
	 *
	 * @param filter the filter
	 * @return the first index found; otherwise -1
	 */
	public int indexOf(Filter<Entry> filter) {
		List<MenuItemNode> nodes = getMenuItemNodes();
		int d = 0;
		for (MenuItemNode node : nodes) {
			if (filter.accept(new Entry(node))) {
				return d;
			}
			d++;
		}
		return -1;
	}

	/**
	 * Hovers the first index of the specified filter.
	 *
	 * @param filter the filter
	 * @return <tt>true</tt> if an entry was hovered, otherwise <tt>false</tt>
	 */
	public boolean hover(Filter<Entry> filter) {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		int index = indexOf(filter);
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
				if ((index = indexOf(filter)) == -1) {
					close();
					return false;
				}
			}
		}
		return hoverIndex(client, index);
	}

	/**
	 * Clicks the first index of the specified flter.
	 *
	 * @param filter the filter
	 * @return <tt>true</tt> if the entry was clicked; otherwise <tt>false</tt>
	 */
	public boolean click(Filter<Entry> filter) {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		int index = indexOf(filter);
		if (index == -1) {
			return false;
		}
		if (!client.isMenuOpen() && index == 0) {
			return ctx.mouse.click(true);
		}
		return hover(filter) && ctx.mouse.click(true);
	}

	/**
	 * Closes the menu.
	 *
	 * @return <tt>true</tt> if the menu was closed, otherwise <tt>false</tt>
	 */
	public boolean close() {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		if (client.isMenuOpen()) {
			ctx.mouse.move(client.getMenuX() + Random.nextInt(-30, -10), Math.max(4, client.getMenuY() + Random.nextInt(-10, 10)));
		}
		return !client.isMenuOpen();
	}

	private boolean hoverIndex(final Client client, int index) {
		int _index = 0, main = 0;
		final NodeSubQueue menu;
		collapsed:
		if (client.isMenuCollapsed()) {
			if ((menu = client.getCollapsedMenuItems()) != null) {
				final Queue<MenuGroupNode> groups = new Queue<>(menu, MenuGroupNode.class);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext(), ++main) {
					int sub = 0;
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<>(queue, MenuItemNode.class);
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
				final Queue<MenuGroupNode> groups = new Queue<>(menu, MenuGroupNode.class);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext()) {
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<>(queue, MenuItemNode.class);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext()) {
						nodes.add(node);
					}
				}
			}
		} else {
			final NodeDeque menu = client.getMenuItems();
			if (menu != null) {
				final Deque<MenuItemNode> deque = new Deque<>(menu, MenuItemNode.class);
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

	/**
	 * Returns an array of all the current menu items ([action_1 option_1, action_2 option_2, ...]).
	 *
	 * @return the array of menu items
	 */
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
