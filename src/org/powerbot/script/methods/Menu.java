package org.powerbot.script.methods;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.client.Client;
import org.powerbot.client.MenuGroupNode;
import org.powerbot.client.MenuItemNode;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeSubQueue;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.internal.wrappers.Queue;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.util.StringUtils;
import org.powerbot.util.math.Vector2;

/**
 * Utilities pertaining to the in-game menu.
 *
 * @author Timer
 */
public class Menu extends MethodProvider {
	private final Object LOCK = new Object();
	private String[] actions = new String[0];
	private String[] options = new String[0];

	public Menu(final MethodContext factory) {
		super(factory);
	}

	public static class Entry {
		public final String action, option;

		protected Entry(final String a, final String o) {
			action = a != null ? StringUtils.stripHtml(a) : "";
			option = o != null ? StringUtils.stripHtml(o) : "";
		}

		@Override
		public String toString() {
			return String.format("%s %s", action, option).trim();
		}
	}

	/**
	 * Creates a filter obeying the provided action.
	 *
	 * @param action the action to filter
	 * @return the filter
	 */
	public static Filter<Entry> filter(final String action) {
		return filter(action, null);
	}

	/**
	 * Creates a filter obeying the provided action and option.
	 *
	 * @param action the action to filter
	 * @param option the option to filter
	 * @return the filter
	 */
	public static Filter<Entry> filter(final String action, final String option) {
		final String a = action != null ? action.toLowerCase() : null;
		final String o = option != null ? option.toLowerCase() : null;
		return new Filter<Entry>() {
			@Override
			public boolean accept(final Entry entry) {
				return (a == null || entry.action.toLowerCase().contains(a)) &&
						(o == null || o.equalsIgnoreCase("null") || entry.option.toLowerCase().contains(o));
			}
		};
	}

	/**
	 * Determines if the menu is open.
	 *
	 * @return <tt>true</tt> if the menu is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen() {
		final Client client = ctx.getClient();
		return client != null && client.isMenuOpen();
	}

	/**
	 * Determines the first index of a specified filter.
	 *
	 * @param filter the filter
	 * @return the first index found; otherwise -1
	 */
	public int indexOf(final Filter<Entry> filter) {
		if (!ctx.game.isLoggedIn()) {
			cache();
		}

		final String[] actions;
		final String[] options;
		synchronized (LOCK) {
			actions = this.actions;
			options = this.options;
		}
		final int len = Math.min(actions.length, options.length);
		for (int i = 0; i < len; i++) {
			if (filter.accept(new Entry(actions[i], options[i]))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Hovers the first index of the specified filter.
	 *
	 * @param filter the filter
	 * @return <tt>true</tt> if an entry was hovered, otherwise <tt>false</tt>
	 */
	public boolean hover(final Filter<Entry> filter) {
		return select(filter, false);
	}

	/**
	 * Clicks the first index of the specified filter.
	 *
	 * @param filter the filter
	 * @return <tt>true</tt> if the entry was clicked; otherwise <tt>false</tt>
	 */
	public boolean click(final Filter<Entry> filter) {
		return select(filter, true);
	}

	private boolean select(final Filter<Entry> filter, final boolean click) {
		final Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		int index = indexOf(filter);
		if (index == -1) {
			return false;
		}

		if (click && !client.isMenuOpen() && index == 0) {
			return ctx.mouse.click(true);
		}

		if (!client.isMenuOpen()) {
			if (ctx.mouse.click(false)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return client.isMenuOpen();
					}
				}, 5, 20);
				sleep(Random.nextInt(0, 300) + 105 * (int) (Math.log(index * 2) / Math.log(2)));

				if (!client.isMenuOpen()) {
					return false;
				}
				if ((index = indexOf(filter)) == -1) {
					close();
					return false;
				}
			}
		}

		final Point p = hoverIndex(client, index);
		return p.getX() != -1 && p.getY() != -1 && (!click || ctx.mouse.click(p, true));
	}

	/**
	 * Closes the menu.
	 *
	 * @return <tt>true</tt> if the menu was closed, otherwise <tt>false</tt>
	 */
	public boolean close() {
		final Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		if (!client.isMenuOpen()) {
			return true;
		}

		final Dimension d = ctx.game.getDimensions();
		final int mx = client.getMenuX(), my = client.getMenuY();
		final int w = (int) d.getWidth(), h = (int) d.getHeight();
		int x1, x2;
		final int y1, y2;
		x1 = x2 = mx;
		y1 = y2 = Math.min(h - 5, Math.max(4, my + Random.nextInt(-10, 10)));
		x1 = Math.max(4, x1 + Random.nextInt(-30, -10));
		x2 = x2 + client.getMenuWidth() +
				(client.isMenuCollapsed() ? client.getSubMenuWidth() : 0) + Random.nextInt(10, 30);
		if (x2 <= w - 5 && (x1 - mx >= 5 || Random.nextBoolean())) {
			ctx.mouse.move(x2, y2);
		} else {
			ctx.mouse.move(x1, y1);
		}
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return client.isMenuOpen();
			}
		}, 10, 50);
	}

	private Point hoverIndex(final Client client, final int index) {
		int _index = 0, main = 0;
		final NodeSubQueue menu;
		collapsed:
		if (client.isMenuCollapsed()) {
			if ((menu = client.getCollapsedMenuItems()) != null) {
				final Queue<MenuGroupNode> groups = new Queue<MenuGroupNode>(menu, MenuGroupNode.class);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext(), ++main) {
					int sub = 0;
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<MenuItemNode>(queue, MenuItemNode.class);
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
			return new Point(-1, -1);
		}
		final Point p = new Point(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * index + Random.nextInt(2, 15))
		);
		return ctx.mouse.move(p) && client.isMenuOpen() ? p : new Point(-1, -1);
	}

	private Point hoverSub(final Client client, final int main, final int sub) {
		final Vector2 dv = new Vector2(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * main + Random.nextInt(2, 15))
		);
		final Vector2 mv = new Vector2(ctx.mouse.getLocation());
		if (mv.get2DDistanceTo(dv) > 200) {
			Vector2 div = dv.add(mv.mul(-1d));
			div = div.mul(Random.nextDouble(0.45, 0.55));
			ctx.mouse.move(mv.add(div).to2DPoint());
		}
		if (ctx.mouse.move(dv.to2DPoint())) {
			sleep(Random.nextInt(125, 175));
			if (client.isMenuOpen()) {
				final Point p = ctx.mouse.getLocation();
				final int cX;
				final int subX = client.getSubMenuX();
				if (ctx.mouse.move(cX = subX + Random.nextInt(4, client.getSubMenuWidth() - 5), p.y)) {
					sleep(Random.nextInt(125, 175));
					if (client.isMenuOpen()) {
						final int subY = client.getSubMenuY();
						final Point p2 = new Point(cX, subY + (16 * sub + Random.nextInt(2, 15) + 21));
						if (ctx.mouse.move(p2)) {
							sleep(Random.nextInt(125, 175));
							return client.isMenuOpen() ? p2 : new Point(-1, -1);
						}
					}
				}
			}
		}
		if (client.isMenuOpen()) {
			close();
		}
		return new Point(-1, -1);
	}

	private List<MenuItemNode> getMenuItemNodes() {
		final List<MenuItemNode> nodes = new LinkedList<MenuItemNode>();

		final Client client = ctx.getClient();
		if (client == null) {
			return nodes;
		}

		final boolean collapsed;
		if (collapsed = client.isMenuCollapsed()) {
			final NodeSubQueue menu = client.getCollapsedMenuItems();
			if (menu != null) {
				final Queue<MenuGroupNode> groups = new Queue<MenuGroupNode>(menu, MenuGroupNode.class);
				for (MenuGroupNode group = groups.getHead(); group != null; group = groups.getNext()) {
					final NodeSubQueue queue;
					if ((queue = group.getItems()) == null) {
						continue;
					}
					final Queue<MenuItemNode> queue2 = new Queue<MenuItemNode>(queue, MenuItemNode.class);
					for (MenuItemNode node = queue2.getHead(); node != null; node = queue2.getNext()) {
						nodes.add(node);
					}
				}
			}
		} else {
			final NodeDeque menu = client.getMenuItems();
			if (menu != null) {
				final Deque<MenuItemNode> deque = new Deque<MenuItemNode>(menu, MenuItemNode.class);
				for (MenuItemNode node = deque.getHead(); node != null; node = deque.getNext()) {
					nodes.add(node);
				}
			}
		}
		if (nodes.size() > 1) {
			final MenuItemNode node = nodes.get(0);
			final String action = node.getAction();
			if (action != null && StringUtils.stripHtml(action).equalsIgnoreCase(collapsed ? "Walk here" : "Cancel")) {
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
		if (!ctx.game.isLoggedIn()) {
			cache();
		}

		final String[] actions;
		final String[] options;
		synchronized (LOCK) {
			actions = this.actions;
			options = this.options;
		}
		final int len = Math.min(actions.length, options.length);
		final String[] arr = new String[len];
		for (int i = 0; i < len; i++) {
			arr[i] = actions[i] + " " + options[i];
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	public void cache() {
		synchronized (LOCK) {
			final List<MenuItemNode> items = getMenuItemNodes();
			final int size = items.size();
			final String[] actions = new String[size];
			final String[] options = new String[size];
			for (int i = 0; i < size; i++) {
				final MenuItemNode node = items.get(i);
				actions[i] = node.getAction();
				options[i] = node.getOption();
			}

			Menu.this.actions = actions;
			Menu.this.options = options;
		}
	}
}
