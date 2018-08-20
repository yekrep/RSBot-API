package org.powerbot.script.rt4;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Random;
import org.powerbot.script.StringUtils;

/**
 * Menu
 *
 * An interface of the menu which appears when right-clicking in the game. An example
 * of this menu is:
 *  <ul>
 *      <li><b>Walk here</b></li>
 *      <li><b>Examine</b> Tree</li>
 *      <li><b>Drop</b> Coins</li>
 *  </ul>
 */
public class Menu extends ClientAccessor {
	private final AtomicBoolean registered;
	private final AtomicReference<String[]> actions, options;

	public Menu(final ClientContext ctx) {
		super(ctx);
		registered = new AtomicBoolean(false);
		final String[] e = new String[0];
		actions = new AtomicReference<String[]>(e);
		options = new AtomicReference<String[]>(e);
	}

	public static Filter<? super MenuCommand> filter(final String action) {
		return filter(action, null);
	}

	public static Filter<? super MenuCommand> filter(final String action, final String option) {
		final String a = action != null ? action.toLowerCase() : null;
		final String o = option != null ? option.toLowerCase() : null;
		return new Filter<MenuCommand>() {
			@Override
			public boolean accept(final MenuCommand command) {
				return (a == null || command.action.toLowerCase().contains(a)) &&
						(o == null || command.option.toLowerCase().contains(o));
			}
		};
	}

	/**
	 * The dimensions of the menu bounds.
	 *
	 * @return A rectangle representing the dimensions of the menu.
	 */
	public Rectangle bounds() {
		final Client client = ctx.client();
		if (client == null || !opened()) {
			return new Rectangle(-1, -1, -1, -1);
		}
		return new Rectangle(client.getMenuX(), client.getMenuY(), client.getMenuWidth(), client.getMenuHeight());
	}

	/**
	 * Whether or not the menu is opened.
	 *
	 * @return {@code true} if the mennu is opened, {@code false} otherwise.
	 */
	public boolean opened() {
		final Client client = ctx.client();
		return client != null && client.isMenuOpen();
	}

	/**
	 * Provides the index of the menu command given the specified filter.
	 *
	 * @param filter The filter to apply to the menu.
	 * @return The index of the menu command, or {@code -1} if it was not found.
	 */
	public int indexOf(final Filter<? super MenuCommand> filter) {
		final String[] actions = this.actions.get(), options = this.options.get();
		final int len;
		if ((len = actions.length) != options.length) {
			return -1;
		}
		for (int i = 0; i < len; i++) {
			if (filter.accept(new MenuCommand(actions[i], options[i]))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Attempts to hover over the menu command, given the provided filter.
	 *
	 * @param filter The filter to apply to the menu.
	 * @return {@code true} if the mouse is within the bounds of the specified MenuCommand,
	 * {@code false} otherwise.
	 */
	public boolean hover(final Filter<? super MenuCommand> filter) {
		return click(filter, false);
	}

	/**
	 * Attempts to click the menu command provided by the filter.
	 *
	 * @param filter The filter to apply to the menu.
	 * @return {@code true} if the mouse has successfully clicked within the bounds of the
	 * MenuCommand.
	 */
	public boolean click(final Filter<? super MenuCommand> filter) {
		return click(filter, true);
	}

	/**
	 * Attempts to click the menu command provided by the filter.
	 *
	 * @param filter The filter to apply to the menu.
	 * @param click Whether or not to left-click.
	 * @return {@code true} if the mouse has successfully clicked within the bounds of the
	 * MenuCommand.
	 */
	private boolean click(final Filter<? super MenuCommand> filter, final boolean click) {
		final Client client = ctx.client();
		int idx;
		if (client == null || (idx = indexOf(filter)) == -1) {
			return false;
		}
		if (click && !client.isMenuOpen() && idx == 0) {
			return ctx.input.click(true);
		}

		if (!client.isMenuOpen()) {
			if (!ctx.input.click(false)) {
				return false;
			}
		}
		if (!Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return client.isMenuOpen();
			}
		}, 15, 10) || (idx = indexOf(filter)) == -1) {
			return false;
		}
		final Rectangle rectangle = new Rectangle(client.getMenuX(), client.getMenuY() + 19 + idx * 15, client.getMenuWidth(), 15);
		Condition.sleep(Random.hicks(idx));
		if (!ctx.input.move(
				Random.nextInt(rectangle.x, rectangle.x + rectangle.width),
				Random.nextInt(rectangle.y, rectangle.y + rectangle.height)) || !client.isMenuOpen()) {
			return false;
		}
		final Point p = ctx.input.getLocation();
		return client.isMenuOpen() && rectangle.contains(p) && (!click || ctx.input.click(true));
	}

	/**
	 * Attempts to close the menu.
	 *
	 * @return {@code true} if the menu was closed, {@code false} otherwise.
	 */
	public boolean close() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		if (!client.isMenuOpen()) {
			return true;
		}

		final Component c = ((InputSimulator) ctx.input).getComponent();
		final Dimension d = new Dimension(c != null ? c.getWidth() : 0, c != null ? c.getHeight() : 0);
		final int mx = client.getMenuX(), my = client.getMenuY();
		final int w = (int) d.getWidth(), h = (int) d.getHeight();
		int x1, x2;
		final int y1, y2;
		x1 = x2 = mx;
		y1 = y2 = Math.min(h - 5, Math.max(4, my + Random.nextInt(-10, 10)));
		x1 = Math.max(4, x1 + Random.nextInt(-30, -10));
		x2 = x2 + client.getMenuWidth() + Random.nextInt(10, 30);
		if (x2 <= w - 5 && (x1 - mx >= 5 || Random.nextBoolean())) {
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

	/**
	 * Returns an array of the current menu commands available.
	 *
	 * @return A MenuCommand array.
	 */
	public MenuCommand[] commands() {
		final String[] actions = this.actions.get(), options = this.options.get();
		final int len;
		if ((len = actions.length) != options.length) {
			return new MenuCommand[0];
		}
		final MenuCommand[] arr = new MenuCommand[len];
		for (int i = 0; i < len; i++) {
			arr[i] = new MenuCommand(actions[i], options[i]);
		}
		return arr;
	}

	public void register() {
		if (!registered.compareAndSet(false, true)) {
			return;
		}
		new Thread(() -> {
			String lastOption = null;
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(40);
				} catch (final InterruptedException ignored) {
					break;
				}

				final Client client = ctx.client();
				if (client == null) {
					continue;
				}

				final String[] actions = client.getMenuActions(), options = client.getMenuOptions();
				if (actions == null || options == null) {
					Menu.this.actions.set(new String[0]);
					Menu.this.options.set(new String[0]);
					continue;
				}
				final int count = client.getMenuCount() / 15;
				final String[] actions2 = new String[count], options2 = new String[count];
				int d = count - 1;
				for (int i = 0; i < Math.min(count, Math.min(actions.length, options.length)); ++i) {
					actions2[d] = StringUtils.stripHtml(actions[i]);
					options2[d] = StringUtils.stripHtml(options[i]);
					--d;
				}

				if(actions2.length > 0) {
					if (actions2[0] != null && lastOption != null && actions2[0] != lastOption) {
						lastOption = null;
						continue;
					}
					lastOption = actions2[0];
				}

				Menu.this.actions.set(actions2);
				Menu.this.options.set(options2);

			}
		}).start();
	}

	@Deprecated
	public static class Command extends MenuCommand {
		protected Command(final String a, final String o) {
			super(a, o);
		}
	}
}
