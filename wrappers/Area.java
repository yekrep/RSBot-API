package org.powerbot.game.api.wrappers;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * @author Timer
 */
public class Area {
	private final Polygon polygon;
	private int plane = -1;

	public Area(final Tile t1, final Tile t2) {
		this(new Tile[]{
				new Tile(Math.min(t1.x, t2.x), Math.min(t1.y, t2.y), t1.plane),
				new Tile(Math.max(t1.x, t2.x), Math.min(t1.y, t2.y), t1.plane),
				new Tile(Math.max(t1.x, t2.x), Math.max(t1.y, t2.y), t2.plane),
				new Tile(Math.min(t1.x, t2.x), Math.max(t1.y, t2.y), t2.plane)
		});
	}

	public Area(final Tile[] bounds) {
		polygon = new Polygon();
		for (final Tile tile : bounds) {
			if (plane != -1 && tile.plane != plane) {
				throw new RuntimeException("area does not support 3d");
			}
			plane = tile.plane;
			addTile(tile.x, tile.y);
		}
	}

	public void translate(final int x, final int y) {
		polygon.translate(x, y);
	}

	public Rectangle getBounds() {
		return polygon.getBounds();
	}

	public int getPlane() {
		return plane;
	}

	public void addTile(final int x, final int y) {
		polygon.addPoint(x, y);
	}

	public boolean contains(final Tile tile) {
		return plane != -1 && plane == tile.getPlane() && polygon.contains(tile.x, tile.y);
	}
}

