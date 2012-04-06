package org.powerbot.game.api.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;

/**
 * @author Timer
 */
public class Tile implements Entity {
	protected final int x, y, plane;

	public Tile(final int x, final int y, final int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		return plane;
	}

	public Tile derive(final int x, final int y) {
		return new Tile(this.x + x, this.y + y, this.plane);
	}

	public boolean validate() {
		final int x = this.x - Game.getBaseX();
		final int y = this.y - Game.getBaseY();
		return x > 0 && x < 104 && y > 0 && y < 104;
	}

	public Point getCentralPoint() {
		return getPoint(0.5d, 0.5d, 0);
	}

	public Point getNextViewportPoint() {
		return getPoint(Random.nextDouble(), Random.nextDouble(), 0);
	}

	public boolean contains(final Point point) {
		final Polygon[] polygons = getBounds();
		return polygons.length == 1 && polygons[0].contains(point);
	}

	public boolean isOnScreen() {
		return getBounds().length == 1;
	}

	public Polygon[] getBounds() {
		final Point localPoint1 = getPoint(0.0D, 0.0D, 0);
		final Point localPoint2 = getPoint(1.0D, 0.0D, 0);
		final Point localPoint3 = getPoint(0.0D, 1.0D, 0);
		final Point localPoint4 = getPoint(1.0D, 1.0D, 0);
		if (Calculations.isPointOnScreen(localPoint1) && Calculations.isPointOnScreen(localPoint2) &&
				Calculations.isPointOnScreen(localPoint3) && Calculations.isPointOnScreen(localPoint4)) {
			final Polygon localPolygon = new Polygon();
			localPolygon.addPoint(localPoint1.x, localPoint1.y);
			localPolygon.addPoint(localPoint2.x, localPoint2.y);
			localPolygon.addPoint(localPoint4.x, localPoint4.y);
			localPolygon.addPoint(localPoint3.x, localPoint3.y);
			return new Polygon[]{localPolygon};
		}
		return new Polygon[0];
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
				Mouse.click(left);
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

	public Point getPoint(final double xOff, final double yOff, final int height) {
		return Calculations.groundToScreen((int) ((x - Game.getBaseX() + xOff) * 0x200), (int) ((y - Game.getBaseY() + yOff) * 0x200), plane, -height);
	}

	public void draw(final Graphics render) {
		//TODO
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(x).append(", ").append(y).append(')').toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof Tile) {
			final Tile tile = (Tile) o;
			return x == tile.x && y == tile.y && plane == tile.plane;
		}
		return false;
	}
}
