package org.powerbot.game.api.wrappers.node;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Mobile;
import org.powerbot.game.api.wrappers.RegionTile;
import org.powerbot.game.api.wrappers.Tile;

/**
 * @author Timer
 */
public class GroundItem implements Entity, Mobile {
	private final Tile tile;
	private final RegionTile localTile;
	private final Item groundItem;

	public GroundItem(final Tile tile, final Item groundItem) {
		this.tile = tile;
		this.localTile = new RegionTile(tile.getX() - Game.getBaseX(), tile.getY() - Game.getBaseY(), tile.getPlane());
		this.groundItem = groundItem;
	}

	public RegionTile getRegionPosition() {
		return localTile;
	}

	public Tile getPosition() {
		return tile;
	}

	public Item getGroundItem() {
		return groundItem;
	}

	public boolean validate() {
		return tile.validate() && groundItem.getId() != -1;
	}

	public Point getCentralPoint() {
		return tile.getCentralPoint();
	}

	public Point getNextViewportPoint() {
		return tile.getCentralPoint();
	}

	public boolean contains(final Point point) {
		return getCentralPoint().distance(point) < 3;
	}

	public boolean isOnScreen() {
		return Calculations.isPointOnScreen(getCentralPoint());
	}

	public Polygon[] getBounds() {
		return tile.getBounds();
	}

	public boolean hover() {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public boolean click(final boolean left) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				Mouse.click(true);
				return true;
			}
		});
	}

	public boolean interact(final String action) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action);
			}
		});
	}

	public boolean interact(final String action, final String option) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action, option);
			}
		});
	}

	public void draw(final Graphics render) {
		//TODO
	}
}
