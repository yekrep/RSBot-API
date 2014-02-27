package org.powerbot.script.tools;

import java.awt.Point;

import org.powerbot.util.StringUtils;

public class Item extends Interactive implements Displayable, Identifiable, Nameable, Stackable {
	private final int id;
	private int stack;
	private final Component component;

	public Item(final MethodContext ctx, final Component component) {
		this(ctx, component.getItemId(), component.getItemStackSize(), component);
	}

	public Item(final MethodContext ctx, final int id, final int stack, final Component component) {
		super(ctx);
		this.id = id;
		this.stack = stack;
		this.component = component;
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int getStackSize() {
		if (component == null) {
			return stack;
		}
		final int stack = component.getItemStackSize();
		if (component.isVisible() && component.getItemId() == this.id) {
			return this.stack = stack;
		}
		return this.stack;
	}

	@Override
	public String getName() {
		final String name;
		if (component != null && component.getItemId() == this.id) {
			name = component.getItemName();
		} else {
			name = ItemDefinition.getDef(ctx, this.id).getName();
		}
		return StringUtils.stripHtml(name);
	}

	public boolean isMembers() {
		return ItemDefinition.getDef(ctx, getId()).isMembers();
	}

	public String[] getActions() {
		return ItemDefinition.getDef(ctx, getId()).getActions();
	}

	public String[] getGroundActions() {
		return ItemDefinition.getDef(ctx, getId()).getGroundActions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponent() {
		return component;
	}

	@Override
	public Point getInteractPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		return component.getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		return component.getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		return component.getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return component != null && component.contains(point);
	}

	@Override
	public boolean isValid() {
		return this.id != -1 && this.component != null && this.component.isValid() &&
				(!this.component.isVisible() || this.component.getItemId() == this.id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + id + "/" + stack + "]@" + component;
	}

	@Override
	public int hashCode() {
		if (component == null) {
			return -1;
		}
		return this.id * 31 + this.component.getIndex();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) {
			return false;
		}
		final Item i = (Item) o;
		return this.id == i.id &&
				((this.component != null && this.component.equals(i.component))
						|| (this.component == null && i.component == null));
	}
}
