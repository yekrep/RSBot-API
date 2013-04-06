package org.powerbot.script.xenon.wrappers;

import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.script.xenon.Calculations;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.util.Random;

public class Tile extends Interactive implements Locatable {
	public int x, y, plane;

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
		return base != null ? Calculations.groundToScreen((int) ((x - base.x + modX) * 512d), (int) ((y - base.y + modY) * 512d), plane, height) : new Point(-1, -1);
	}

	public Point toMap() {
		return Calculations.worldToMap(getX(), getY());
	}

	public boolean isOnMap() {
		final Point p = toMap();
		return p.x != -1 && p.y != -1;
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
		if (Calculations.isPointOnScreen(topLeft) && Calculations.isPointOnScreen(topRight) &&
				Calculations.isPointOnScreen(bottomRight) && Calculations.isPointOnScreen(bottomLeft)) {
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
		return true;//TODO this
	}

	/*
	@Override
	public boolean isValid() {
		final Client client = Bot.client();
		final int plane;
		final Tile base = Game.getMapBase();
		if (client == null || base == null || this.plane != (plane = client.getPlane())) return false;
		final int localX = x - base.getX(), localY = y - base.getY();
		if (x < 0 || y < 0) return false;
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] groundDataArr = info != null ? info.getGroundData() : null;
		final RSGroundData groundData;
		final int[][] blocks;
		if (groundDataArr != null && plane >= 0 && plane < groundDataArr.length && (groundData = groundDataArr[plane]) != null &&
				(blocks = groundData.getBlocks()) != null) {
			final int x = groundData.getX(), y = groundData.getY();
			final int tX = localX - x, tY = localY - y;
			return tX >= 0 && tY >= 0 && tX < blocks.length && tY < blocks[tX].length;
		}
		return false;
	}
	*/

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
