package org.powerbot.os.api.methods;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.os.api.util.Condition;
import org.powerbot.os.api.util.Filter;
import org.powerbot.os.api.util.Random;
import org.powerbot.os.bot.event.PaintListener;
import org.powerbot.os.client.Client;
import org.powerbot.os.util.StringUtils;

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

	public static Filter<Command> filter(final String action) {
		return filter(action, null);
	}

	public static Filter<Command> filter(final String action, final String option) {
		final String a = action != null ? action.toLowerCase() : null;
		final String o = option != null ? option.toLowerCase() : null;
		return new Filter<Command>() {
			@Override
			public boolean accept(final Command command) {
				return (a == null || command.action.toLowerCase().contains(a)) &&
						(o == null || command.option.toLowerCase().contains(o));
			}
		};
	}

	public Rectangle getBounds() {
		final Client client = ctx.client();
		if (client == null || !isOpen()) {
			return new Rectangle(-1, -1, -1, -1);
		}
		return new Rectangle(client.getMenuX(), client.getMenuY(), client.getMenuWidth(), client.getMenuHeight());
	}

	public boolean isOpen() {
		final Client client = ctx.client();
		return client != null && client.isMenuOpen();
	}

	public int indexOf(final Filter<Command> filter) {
		final String[] actions = this.actions.get(), options = this.options.get();
		final int len;
		if ((len = actions.length) != options.length) {
			return -1;
		}
		for (int i = 0; i < len; i++) {
			if (filter.accept(new Command(actions[i], options[i]))) {
				return i;
			}
		}
		return -1;
	}

	public boolean hover(final Filter<Command> filter) {
		final Client client = ctx.client();
		if (client == null || indexOf(filter) == -1) {
			return false;
		}
		if (!client.isMenuOpen()) {
			if (!ctx.mouse.click(false)) {
				return false;
			}
		}
		final int idx;
		if (!Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return client.isMenuOpen();
			}
		}, 15, 10) || (idx = indexOf(filter)) == -1) {
			return false;
		}
		final Rectangle rectangle = new Rectangle(client.getMenuX(), client.getMenuY() + 19 + idx * 15, client.getMenuWidth(), 15);
		return ctx.mouse.move(
				Random.nextInt(rectangle.x, rectangle.x + rectangle.width),
				Random.nextInt(rectangle.y, rectangle.y + rectangle.height)
		) && client.isMenuOpen();
	}

	public boolean click(final Filter<Command> filter) {
		final Client client = ctx.client();
		final int idx;
		if (client == null || !hover(filter) || (idx = indexOf(filter)) == -1) {
			return false;
		}
		final Rectangle rectangle = new Rectangle(client.getMenuX(), client.getMenuY() + 19 + idx * 15, client.getMenuWidth(), 15);
		final Point p = ctx.mouse.getLocation();
		return client.isMenuOpen() && rectangle.contains(p) && ctx.mouse.click(true);
	}

	public boolean close() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		if (!client.isMenuOpen()) {
			return true;
		}

		final InputEngine e = SelectiveEventQueue.getInstance().getEngine();
		final Component c = e != null ? e.getComponent() : null;
		final Dimension d = new Dimension(c != null ? c.getWidth() : 0, c != null ? c.getHeight() : 0);
		final int mx = client.getMenuX(), my = client.getMenuY();
		final int w = (int) d.getWidth(), h = (int) d.getHeight();
		int x1, x2;
		final int y1, y2;
		x1 = x2 = mx;
		y1 = y2 = Math.min(h - 5, Math.max(4, my + Random.nextInt(-10, 10)));
		x1 = Math.max(4, x1 + Random.nextInt(-30, -10));
		x2 = x2 + client.getMenuWidth() + +Random.nextInt(10, 30);
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

	private void register() {
		if (!registered.compareAndSet(false, true)) {
			return;
		}
		ctx.bot().dispatcher.add(new PaintListener() {
			@Override
			public void repaint(final Graphics render) {
				final Client client = ctx.client();
				if (client == null) {
					return;
				}

				final String[] actions = client.getMenuActions(), options = client.getMenuOptions();
				if (actions != null && options != null && actions.length == options.length) {
					Menu.this.actions.set(actions);
					Menu.this.options.set(options);
				}
			}
		});
	}

	public static class Command {
		public final String action, option;

		private Command(final String a, final String o) {
			action = a != null ? StringUtils.stripHtml(a) : "";
			option = o != null ? StringUtils.stripHtml(o) : "";
		}
	}
}
