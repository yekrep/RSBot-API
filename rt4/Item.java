package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Random;
import org.powerbot.script.Stackable;
import org.powerbot.util.StringUtils;

public class Item extends Interactive implements Identifiable, Nameable, Stackable, Actionable {
	private static final int WIDTH = 42, HEIGHT = 36;
	final Component component;
	private final int inventory_index, id;
	private final int stack;

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
		return StringUtils.stripHtml(ItemConfig.getDef(ctx, id).getName());
	}

	@Override
	public int stackSize() {
		return stack;
	}

	public boolean members() {
		return ItemConfig.getDef(ctx, id).isMembers();
	}

	@Override
	public String[] actions() {
		return ItemConfig.getDef(ctx, id).getActions();
	}

	public String[] groundActions() {
		return ItemConfig.getDef(ctx, id).getGroundActions();
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

	@Override
	public boolean valid() {
		return component != null && component.visible() && id != -1;
	}
}