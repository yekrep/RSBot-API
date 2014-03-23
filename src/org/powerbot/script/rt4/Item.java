package org.powerbot.script.rt4;

import java.awt.Point;

import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Stackable;

public class Item extends Interactive implements Identifiable, Nameable, Stackable {
	private final Component component;
	private final int index, id;
	private int stack;

	public Item(final ClientContext ctx, final Component component, final int index, final int id, final int stack) {
		super(ctx);
		this.component = component;
		this.index = index;
		this.id = id;
		this.stack = stack;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Point centerPoint() {
		return null;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public int stackSize() {
		return 0;
	}

	@Override
	public Point nextPoint() {
		return null;
	}

	@Override
	public boolean contains(final Point point) {
		return false;
	}
}
