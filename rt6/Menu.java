package org.powerbot.script.rt6;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.powerbot.bot.rt6.NodeQueue;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.MenuGroupNode;
import org.powerbot.bot.rt6.client.MenuItemNode;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.Random;
import org.powerbot.script.Vector2;
import org.powerbot.script.StringUtils;

/**
 * Utilities pertaining to the in-game menu.
 */
public class Menu extends ClientAccessor {
	public Menu(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Creates a filter obeying the provided action.
	 *
	 * @param action the action to filter
	 * @return the filter
	 */
	public static Filter<MenuCommand> filter(final String action) {
		return filter(action, null);
	}

	/**
	 * Creates a filter obeying the provided action and option.
	 *
	 * @param action the action to filter
	 * @param option the option to filter
	 * @return the filter
	 */
	public static Filter<MenuCommand> filter(final String action, final String option) {
		final String a = action != null ? action.toLowerCase() : null;
		final String o = option != null ? option.toLowerCase() : null;
		return new Filter<MenuCommand>() {
			@Override
			public boolean accept(final MenuCommand command) {
				return (a == null || command.action.toLowerCase().contains(a)) &&
						(o == null || o.equalsIgnoreCase("null") || command.option.toLowerCase().contains(o));
			}
		};
	}

	/**
	 * Determines if the menu is open.
	 *
	 * @return <tt>true</tt> if the menu is open; otherwise <tt>false</tt>
	 */
	public boolean opened() {
		final Client client = ctx.client();
		return client != null && client.isMenuOpen();
	}

	/**
	 * Determines the first index of a specified filter.
	 *
	 * @param filter the filter
	 * @return the first index found; otherwise -1
	 */
	public int indexOf(final Filter<? super MenuCommand> filter) {
		final MenuCommand[] m = commands();
		for (int i = 0; i < m.length; i++) {
			if (filter.accept(m[i])) {
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
	public boolean hover(final Filter<? super MenuCommand> filter) {
		return select(filter, false);
	}

	/**
	 * Clicks the first index of the specified filter.
	 *
	 * @param filter the filter
	 * @return <tt>true</tt> if the entry was clicked; otherwise <tt>false</tt>
	 */
	public boolean click(final Filter<? super MenuCommand> filter) {
		return select(filter, true);
	}

	private boolean select(final Filter<? super MenuCommand> filter, final boolean click) {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		int index = indexOf(filter);
		if (index == -1) {
			return false;
		}

		if (click && !client.isMenuOpen() && !Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return indexOf(filter) != 0;
			}
		}, 10, 10)) {
			return ctx.input.click(true);
		}

		if (!client.isMenuOpen()) {
			if (ctx.input.click(false)) {
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return client.isMenuOpen();
					}
				}, 5, 20);
				Condition.sleep(Random.hicks(index));

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
		return p.getX() != -1 && p.getY() != -1 && (!click || ctx.input.click(p, true));
	}

	/**
	 * Closes the menu.
	 *
	 * @return <tt>true</tt> if the menu was closed, otherwise <tt>false</tt>
	 */
	public boolean close() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		if (!client.isMenuOpen()) {
			return true;
		}

		final Game.Viewport v = ctx.game.getViewport();
		final int mx = client.getMenuX(), my = client.getMenuY();
		int x1, x2;
		final int y1, y2;
		x1 = x2 = mx;
		y1 = y2 = Math.min((int) v.my - 5, Math.max(4, my + Random.nextInt(-10, 10)));
		x1 = Math.max(4, x1 + Random.nextInt(-30, -10));
		x2 = x2 + client.getMenuWidth() +
				(client.isMenuCollapsed() ? client.getSubMenuWidth() : 0) + Random.nextInt(10, 30);
		if (x2 <= (int) v.mx - 5 && (x1 - mx >= 5 || Random.nextBoolean())) {
			ctx.input.move(x2, y2);
		} else {
			ctx.input.move(x1, y1);
		}
		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return client.isMenuOpen();
			}
		}, 10, 50);
	}

	public Rectangle bounds() {
		final Client client = ctx.client();
		if (client == null || !opened()) {
			return new Rectangle(-1, -1, -1, -1);
		}
		return new Rectangle(client.getMenuX(), client.getMenuY(), client.getMenuWidth(), client.getMenuHeight());
	}

	private Point hoverIndex(final Client client, final int index) {
		int _index = 0, main = -1;
		Vector2 addum = new Vector2(0, 16 * index);
		collapsed:
		if (client.isMenuCollapsed()) {
			for (final MenuGroupNode g : NodeQueue.get(client.getCollapsedMenuItems(), MenuGroupNode.class)) {
				main++;
				final List<MenuItemNode> t = NodeQueue.get(g.getItems(), MenuItemNode.class);
				for (int i = 0; i < t.size(); i++) {
					if (_index++ == index) {
						if (i == 0) {
							addum = new Vector2(0, 16 * main);
							break collapsed;
						} else {
							return hoverSub(client, main, i);
						}
					}
				}

			}

			if (client.isMenuOpen()) {
				close();
			}
			return new Point(-1, -1);
		}
		final Vector2 p = new Vector2(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + 21 + Random.nextInt(2, 15)
		).add(addum);
		return ctx.input.move(p.toPoint()) && client.isMenuOpen() ? p.toPoint() : new Point(-1, -1);
	}

	private Point hoverSub(final Client client, final int main, final int sub) {
		final Vector2 dv = new Vector2(
				client.getMenuX() + Random.nextInt(4, client.getMenuWidth() - 5),
				client.getMenuY() + (21 + 16 * main + Random.nextInt(2, 15))
		);
		if (ctx.input.move(dv.toPoint())) {
			Condition.sleep();
			if (client.isMenuOpen()) {
				final Point p = ctx.input.getLocation();
				final int cX;
				final int subX = client.getSubMenuX();
				if (ctx.input.move(cX = subX + Random.nextInt(4, client.getSubMenuWidth() - 5), p.y) && client.isMenuOpen()) {
					final int subY = client.getSubMenuY();
					final Point p2 = new Point(cX, subY + (16 * sub + Random.nextInt(2, 15) + 21));
					if (ctx.input.move(p2)) {
						Condition.sleep();
						return client.isMenuOpen() ? p2 : new Point(-1, -1);
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

		final Client client = ctx.client();
		if (client == null) {
			return nodes;
		}

		final boolean collapsed;
		if (collapsed = client.isMenuCollapsed()) {
			for (final MenuGroupNode n : NodeQueue.get(client.getCollapsedMenuItems(), MenuGroupNode.class)) {
				nodes.addAll(NodeQueue.get(n.getItems(), MenuItemNode.class));
			}
		} else {
			nodes.addAll(NodeQueue.get(client.getMenuItems(), MenuItemNode.class));
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
	public String[] items() {
		final MenuCommand[] m = commands();
		final int len = m.length;
		final String[] arr = new String[len];
		for (int i = 0; i < len; i++) {
			arr[i] = m[i].action + " " + m[i].option;
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	public MenuCommand[] commands() {
		final List<MenuItemNode> items = getMenuItemNodes();
		final int size = items.size();
		final MenuCommand[] arr = new MenuCommand[size];
		for (int i = 0; i < size; i++) {
			final MenuItemNode node = items.get(i);
			arr[i] = new MenuCommand(node.getAction(), node.getOption());
		}
		return arr;
	}

	@Deprecated
	public static class Command extends MenuCommand {
		protected Command(final String a, final String o) {
			super(a, o);
		}
	}
}
