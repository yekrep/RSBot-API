package org.powerbot.script.wrappers;

import java.awt.Polygon;

import org.powerbot.script.lang.Locatable;

public class Area {
	private final Polygon polygon;
	private final int plane;

	public Area(Tile t1, Tile t2) {
		this(
				new Tile(Math.min(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane()),
				new Tile(Math.min(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane())
		);
	}

	public Area(Tile... tiles) {
		if (tiles.length < 0) {
			throw new IllegalArgumentException("tiles.length < 0");
		}
		this.polygon = new Polygon();
		this.plane = tiles[0].getPlane();
		for (Tile tile : tiles) {
			if (tile.getPlane() != this.plane) {
				throw new IllegalArgumentException("mismatched planes " + plane + " != " + tile.getPlane());
			}
			polygon.addPoint(tile.getX(), tile.getY());
		}
	}

	public boolean contains(Locatable... locatables) {
		for (Locatable locatable : locatables) {
			Tile tile = locatable.getLocation();
			if (tile.getPlane() != plane || !polygon.contains(tile.getX(), tile.getY())) {
				return false;
			}
		}
		return true;
	}

	public Tile getCentralTile() {
		return new Tile((int) Math.round(avg(polygon.xpoints)), (int) Math.round(avg(polygon.ypoints)), plane);
	}

	private double avg(final int... nums) {
		long total = 0;
		for (int i : nums) {
			total += (long) i;
		}
		return (double) total / (double) nums.length;
	}
}