package org.powerbot.script.xenon.wrappers;

import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Movement;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.util.Random;

public class Tile extends Interactive implements Locatable {
	public final int x;
	public final int y;
	public final int plane;

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

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
		return derive(x, y, this.plane);
	}

	public Tile derive(final int x, final int y, final int plane) {
		return new Tile(this.x + x, this.y + y, plane);
	}

	public Tile randomize(final int left, final int right, final int down, final int up) {
		return derive(Random.nextInt(left, right + 1), Random.nextInt(down, up + 1));
	}

	public Tile randomize(final int x, final int y) {
		return randomize(-x, x, -y, y);
	}

	public Point getPoint(final int height) {
		return getPoint(0.5d, 0.5d, height);
	}

	public Point getPoint(final double modX, final double modY, final int height) {
		final Tile base = Game.getMapBase();
		return base != null ? Game.groundToScreen((int) ((x - base.x + modX) * 512d), (int) ((y - base.y + modY) * 512d), plane, height) : new Point(-1, -1);
	}

	public Point getMapPoint() {
		return Game.worldToMap(getX() + 0.5d, getY() + 0.5d);
	}

	public boolean isOnMap() {
		final Point p = getMapPoint();
		return p.x != -1 && p.y != -1;
	}

	public boolean canReach() {
		final Player player = Players.getLocal();
		final Tile loc = player != null ? player.getLocation() : null;
		return Movement.getDistance(this, loc, false) != -1;
	}

	@Override
	public Tile getLocation() {
		return this;
	}

	@Override
	public Point getInteractPoint() {
		final int x = Random.nextGaussian(0, 100, 5);
		final int y = Random.nextGaussian(0, 100, 5);
		return getPoint(x / 100.0D, y / 100.0D, 0);
	}

	@Override
	public Point getNextPoint() {
		return getPoint(Random.nextDouble(0.0D, 1.0D), Random.nextDouble(0.0D, 1.0D), 0);
	}

	@Override
	public Point getCenterPoint() {
		return getPoint(0);
	}

	@Override
	public boolean contains(final Point point) {
		final Point topLeft = getPoint(0.0D, 0.0D, 0);
		final Point topRight = getPoint(1.0D, 0.0D, 0);
		final Point bottomRight = getPoint(1.0D, 1.0D, 0);
		final Point bottomLeft = getPoint(0.0D, 1.0D, 0);
		if (Game.isPointOnScreen(topLeft) && Game.isPointOnScreen(topRight) &&
				Game.isPointOnScreen(bottomRight) && Game.isPointOnScreen(bottomLeft)) {
			final Polygon localPolygon = new Polygon();
			localPolygon.addPoint(topLeft.x, topLeft.y);
			localPolygon.addPoint(topRight.x, topRight.y);
			localPolygon.addPoint(bottomRight.x, bottomRight.y);
			localPolygon.addPoint(bottomLeft.x, bottomLeft.y);
			return localPolygon.contains(point);
		}
		return false;
	}

	@Override
	public boolean isValid() {
		final Tile t = Game.getMapBase();
		if (t == null) return false;
		final int x = this.x - t.x, y = this.y - t.y;
		return x >= 0 && y >= 0 && x < 104 && y < 104;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + plane + ')';
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Tile)) return false;
		final Tile t = (Tile) o;
		return x == t.x && y == t.y && plane == t.plane;
	}
}
