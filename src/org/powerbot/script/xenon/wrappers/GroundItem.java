package org.powerbot.script.xenon.wrappers;

import java.awt.Point;

public class GroundItem extends Interactive implements Locatable {//TODO validatable
	private final Tile tile;
	private final Item item;

	public GroundItem(final Tile tile, final Item item) {
		this.tile = tile;
		this.item = item;
	}

	public Item getItem() {
		return this.item;
	}

	@Override
	public Tile getLocation() {
		return tile;
	}

	@Override
	public Point getInteractPoint() {
		return tile.getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		return tile.getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		return tile.getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.contains(point);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GroundItem)) return false;
		final GroundItem g = (GroundItem) o;
		return g.tile.equals(this.tile) && g.item.equals(this.item);
	}
}
