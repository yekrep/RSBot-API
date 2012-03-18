package org.powerbot.game.api.wrappers;

import java.awt.Polygon;

/**
 * @author Timer
 */
public class Area extends Polygon {
	private static final long serialVersionUID = 1L;

	public Area(final Tile t1, final Tile t2) {
		this(new Tile[]{
				new Tile(Math.min(t1.x, t2.x), Math.min(t1.y, t2.y), t1.plane),
				new Tile(Math.max(t1.x, t2.x), Math.min(t1.y, t2.y), t1.plane),
				new Tile(Math.max(t1.x, t2.x), Math.max(t1.y, t2.y), t2.plane),
				new Tile(Math.min(t1.x, t2.x), Math.max(t1.y, t2.y), t2.plane)
		});
	}

	public Area(final Tile[] bounds) {
		int plane = -1;
		for (final Tile tile : bounds) {
			if (plane != -1 && tile.plane != plane) {
				throw new RuntimeException("area does not support 3d");
			}
			plane = tile.plane;
			addPoint(tile.x, tile.y);
		}
	}
}
