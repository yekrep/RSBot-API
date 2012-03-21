package org.powerbot.game.api.wrappers.node;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.LocalTile;
import org.powerbot.game.api.wrappers.Mobile;
import org.powerbot.game.api.wrappers.Tile;

/**
 * @author Timer
 */
public class GroundItem implements Entity, Mobile {
	private final Tile tile;
	private final LocalTile localTile;
	private final Item groundItem;

	public GroundItem(final Tile tile, final Item groundItem) {
		this.tile = tile;
		this.localTile = new LocalTile(tile.x - Game.getBaseX(), tile.y - Game.getBaseY(), tile.plane);
		this.groundItem = groundItem;
	}

	public LocalTile getLocalPosition() {
		return localTile;
	}

	public Tile getPosition() {
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
