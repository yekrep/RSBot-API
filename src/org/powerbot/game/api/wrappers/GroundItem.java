package org.powerbot.game.api.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * @author Timer
 */
public class GroundItem implements Entity {
	private final Tile tile;
	private final Item groundItem;

	public GroundItem(final Tile tile, final Item groundItem) {
		this.tile = tile;
		this.groundItem = groundItem;
	}

	public Tile getLocation() {
		return tile;
	}

	public Item getGroundItem() {
		return groundItem;
	}

	public boolean verify() {
		return false;//TODO
	}

	public Point getCentralPoint() {
		return null;//TODO
	}

	public Point getNextViewportPoint() {
		return null;//TODO
	}

	public boolean contains(final Point point) {
		return false;//TODO
	}

	public boolean isOnScreen() {
		return false;//TODO
	}

	public Polygon[] getBounds() {
		return new Polygon[0];//TODO
	}

	public boolean hover() {
		return false;//TODO
	}

	public boolean click(final boolean left) {
		return false;//TODO
	}

	public boolean interact(final String action) {
		return false;//TODO
	}

	public boolean interact(final String action, final String option) {
		return false;//TODO
	}

	public void draw(final Graphics render) {
		//TODO
	}
}
