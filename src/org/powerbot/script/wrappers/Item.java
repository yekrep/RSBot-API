package org.powerbot.script.wrappers;

import java.awt.Point;

import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.lang.Nameable;
import org.powerbot.script.lang.Stackable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.util.StringUtil;

public class Item extends Interactive implements Identifiable, Nameable, Stackable {
	private final int id;
	private int stack;
	private final Component component;

	public Item(MethodContext ctx, Component component) {
		this(ctx, component.getItemId(), component.getItemStackSize(), component);
	}

	public Item(MethodContext ctx, int id, int stack, Component component) {
		super(ctx);
		this.id = id;
		this.stack = stack;
		this.component = component;
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
		int stack = component.getItemStackSize();
		if (component.isVisible() && component.getItemId() == this.id) {
			return this.stack = stack;
		}
		return this.stack;
	}

	@Override
	public String getName() {
		String name;
		if (component != null && component.getItemId() == this.id) {
			name = component.getItemName();
		} else {
			name = ItemDefinition.getDef(ctx, this.id).getName();
		}
		return StringUtil.stripHtml(name);
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
	public boolean contains(Point point) {
		if (component == null) {
			return false;
		}
		return component.contains(point);
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
		return this.id == i.id && this.component != null && this.component.equals(i.component);
	}
}
