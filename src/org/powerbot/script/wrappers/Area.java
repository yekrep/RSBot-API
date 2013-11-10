package org.powerbot.script.wrappers;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

import org.powerbot.script.util.Random;

public class Area {
	private final Polygon polygon;
	private final int plane;
	private Tile[] tiles;

	public Area(final Tile t1, final Tile t2) {
		this(
				new Tile(Math.min(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane()),
				new Tile(Math.min(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane())
		);
	}

	public Area(final Tile... tiles) {
		if (tiles.length < 0) {
			throw new IllegalArgumentException("tiles.length < 0");
		}
		this.polygon = new Polygon();
		this.plane = tiles[0].getPlane();
		for (final Tile tile : tiles) {
			if (tile.getPlane() != this.plane) {
				throw new IllegalArgumentException("mismatched planes " + plane + " != " + tile.getPlane());
			}
			polygon.addPoint(tile.getX(), tile.getY());
		}
		this.tiles = null;
	}

	public boolean contains(final Locatable... locatables) {
		for (final Locatable locatable : locatables) {
			final Tile tile = locatable.getLocation();
			if (tile.getPlane() != plane || !polygon.contains(tile.getX(), tile.getY())) {
				return false;
			}
		}
		return true;
	}

	public Tile getCentralTile() {
		return new Tile((int) Math.round(avg(polygon.xpoints)), (int) Math.round(avg(polygon.ypoints)), plane);
	}

	public Tile getRandomTile() {
		final Tile[] tiles = getTiles();
		final int len = tiles.length;
		return len != 0 ? tiles[Random.nextInt(0, len)] : Tile.NIL;
	}

	public Tile getClosestTo(final Locatable locatable) {
		final Tile t = locatable != null ? locatable.getLocation() : Tile.NIL;
		if (t != Tile.NIL) {
			double dist = Double.POSITIVE_INFINITY;
			Tile tile = Tile.NIL;
			final Tile[] tiles = getTiles();
			for (int i = 0; i < tiles.length; i++) {
				final double d = t.distanceTo(tiles[i]);
				if (d < dist) {
					dist = d;
					tile = tiles[i];
				}
			}
			return tile;
		}
		return Tile.NIL;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	private Tile[] getTiles() {
		if (this.tiles != null) {
			return this.tiles;
		}

		final Rectangle r = polygon.getBounds();
		int c = 0;
		final Tile[] tiles = new Tile[r.width * r.height];
		for (int x = 0; x < r.width; x++) {
			for (int y = 0; y < r.height; y++) {
				final int _x = r.x + x;
				final int _y = r.y + y;
				if (polygon.contains(_x, _y)) {
					tiles[c++] = new Tile(_x, _y, plane);
				}
			}
		}
		return this.tiles = Arrays.copyOf(tiles, c);
	}

	private double avg(final int... nums) {
		long total = 0;
		for (final int i : nums) {
			total += (long) i;
		}
		return (double) total / (double) nums.length;
	}
}