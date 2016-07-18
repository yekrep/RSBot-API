package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.script.Actionable;
import org.powerbot.script.Calculations;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Stackable;
import org.powerbot.script.StringUtils;

/**
 * Item
 */
public class Item extends GenericItem implements Identifiable, Nameable, Stackable, Actionable {
	private static final int WIDTH = 42, HEIGHT = 36;
	final Component component;
	private final int inventory_index, id;
	private int stack;

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
		if (component == null) {
			return new Point(-1, -1);
		}
		if (inventory_index != -1) {
			final Point base = component.screenPoint();
			final int x = base.x - 3 + (inventory_index % 4) * WIDTH, y = base.y - 2 + (inventory_index / 4) * HEIGHT;
			return new Point(x + WIDTH / 2, y + HEIGHT / 2);
		}
		return component.centerPoint();
	}

	@Override
	public String name() {
		return StringUtils.stripHtml(super.name());
	}

	@Override
	public int stackSize() {
		if (component == null || !component.valid()) {
			return stack;
		}
		if (inventory_index != -1) {
			final int[] itemIds = component.itemIds();
			final int[] stackSizes = component.itemStackSizes();
			return (itemIds.length > inventory_index && stackSizes.length > inventory_index && itemIds[inventory_index] == id
					? stack = stackSizes[inventory_index]
					: stack);
		}
		if (component.visible() && component.itemId() == id) {
			return stack = component.itemStackSize();
		}
		return stack;
	}

	@Override
	public String[] actions() {
		return inventoryActions();
	}

	@Override
	public Point nextPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		if (inventory_index != -1) {
			final Point base = component.screenPoint();
			final Rectangle r = new Rectangle(base.x - 3 + (inventory_index % 4) * WIDTH, base.y - 2 + (inventory_index / 4) * HEIGHT, WIDTH, HEIGHT);
			return Calculations.nextPoint(r, new Rectangle(r.x + r.width / 2, r.y + r.height / 2, r.width / 4, r.height / 4));
		}
		return component.nextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		if (component == null) {
			return false;
		}
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
		if (id == -1 || component == null || !component.valid()) {
			return false;
		}
		if (inventory_index != -1) {
			final int[] itemIds = component.itemIds();
			return itemIds.length > inventory_index && itemIds[inventory_index] == id;
		}
		return !component.visible() || component.itemId() == id;
	}
}