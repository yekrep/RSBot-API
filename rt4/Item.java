package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.rt4.Bot;
import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Random;
import org.powerbot.script.Stackable;
import org.powerbot.script.StringUtils;

/**
 * Item
 */
public class Item extends Interactive implements Identifiable, Nameable, Stackable, Actionable {
	private static final int WIDTH = 42, HEIGHT = 36;
	final Component component;
	private final int inventory_index, id;
	private final int stack;

	public Item(final ClientContext ctx, final Component component) {
		this(ctx, component, component.itemId(), component.itemStackSize());
	}

	public Item(final ClientContext ctx, final Component component, final int id, final int stack) {
		this(ctx, component, -1, id, stack);
	}

	public Item(final ClientContext ctx, final Component component, final int inventory_index, final int id, final int stack) {
		super(ctx);
		this.component = component;
		this.inventory_index = inventory_index;
		this.id = id;
		this.stack = stack;
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Point centerPoint() {
		if (inventory_index != -1) {
			final Point base = component.screenPoint();
			final int x = base.x - 3 + (inventory_index % 4) * WIDTH, y = base.y - 2 + (inventory_index / 4) * HEIGHT;
			return new Point(x + WIDTH / 2, y + HEIGHT / 2);
		}
		return component.centerPoint();
	}

	@Override
	public String name() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id);
		final String name = c != null ? c.name : "";
		return StringUtils.stripHtml(name);
	}

	@Override
	public int stackSize() {
		return stack;
	}

	public boolean members() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id);
		return c != null && c.members;
	}

	@Override
	public String[] actions() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id);
		return c != null ? c.actions : new String[0];
	}

	public String[] groundActions() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id);
		return c != null ? c.groundActions : new String[0];
	}

	@Override
	public Point nextPoint() {
		if (inventory_index != -1) {
			final Point base = component.screenPoint();
			final Rectangle r = new Rectangle(base.x - 3 + (inventory_index % 4) * WIDTH, base.y - 2 + (inventory_index / 4) * HEIGHT, WIDTH, HEIGHT);
			return new Point(Random.nextInt(r.x, r.x + r.width), Random.nextInt(r.y, r.y + r.height));
		}
		return component.nextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		if (inventory_index != -1) {
			final Point base = component.screenPoint();
			final Rectangle r = new Rectangle(base.x - 3 + (inventory_index % 4) * WIDTH, base.y - 2 + (inventory_index / 4) * HEIGHT, WIDTH, HEIGHT);
			return r.contains(point);
		}
		return component.contains(point);
	}

	public Component component() {
		return component;
	}

	@Override
	public boolean valid() {
		return component != null && component.visible() && id != -1;
	}
}