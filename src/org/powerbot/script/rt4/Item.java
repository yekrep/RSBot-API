package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Random;
import org.powerbot.script.Stackable;

public class Item extends Interactive implements Identifiable, Nameable, Stackable {
	private static final int BASE_X = 560, BASE_Y = 210;
	private static final int WIDTH = 42, HEIGHT = 36;
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
		final int x = BASE_X + (index % 4) * WIDTH, y = BASE_Y + (index / 4) * HEIGHT;
		return new Point(x + WIDTH / 2, y + HEIGHT / 2);
	}

	@Override
	public String name() {
		return "";
	}

	@Override
	public int stackSize() {
		return stack;
	}

	@Override
	public Point nextPoint() {
		final Rectangle r = new Rectangle(BASE_X + (index % 4) * WIDTH, BASE_Y + (index / 4) * HEIGHT, WIDTH, HEIGHT);
		return new Point(Random.nextInt(r.x, r.x + r.width), Random.nextInt(r.y, r.y + r.height));
	}

	@Override
	public boolean contains(final Point point) {
		final Rectangle r = new Rectangle(BASE_X + (index % 4) * WIDTH, BASE_Y + (index / 4) * HEIGHT, WIDTH, HEIGHT);
		return r.contains(point);
	}
}
