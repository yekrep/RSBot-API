package org.powerbot.api.tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;

import org.powerbot.api.ClientContext;
import org.powerbot.api.util.Calculations;

abstract class Cuboid extends Interactive {
	public Cuboid(final ClientContext ctx) {
		super(ctx);
	}

	public abstract int getX();

	public abstract int getZ();

	public abstract int getHeight();

	public abstract Rectangle getBounds();

	protected Area getCuboid(final int d) {
		final Rectangle rectangle = getBounds();
		final int x = getX(), z = getZ(), h = getHeight();
		final int y = ctx.game.getHeight(x, z);
		final Point p = ctx.game.worldToScreen(x, y, z, h);
		if (rectangle.x == -1 || rectangle.y == -1 || x == -1 || z == -1 ||
				p.x == -1 || p.y == -1) {
			return null;
		}
		final int gx = x >> 7, gy = z >> 7;
		final int bx = rectangle.x, by = rectangle.y, bw = rectangle.width, bh = rectangle.height;

		final int size = 128;
		final int gox1 = (gx - bx) * size, goy1 = (gy - by) * size;
		final int gox2 = (bx + bw - gx) * size, goy2 = (by + bh - gy) * size;

		final Point g1 = ctx.game.worldToScreen(x - d - gox1, y, z - d - goy1, 0);
		final Point g2 = ctx.game.worldToScreen(x - d - gox1, y, z + d + goy2, 0);
		final Point g3 = ctx.game.worldToScreen(x + d + gox2, y, z + d + goy2, 0);
		final Point g4 = ctx.game.worldToScreen(x + d + gox2, y, z - d - goy1, 0);
		final Point o1 = ctx.game.worldToScreen(x - d - gox1, y, z - d - goy1, h);
		final Point o2 = ctx.game.worldToScreen(x - d - gox1, y, z + d + goy2, h);
		final Point o3 = ctx.game.worldToScreen(x + d + gox2, y, z + d + goy2, h);
		final Point o4 = ctx.game.worldToScreen(x + d + gox2, y, z - d - goy1, h);
		if (g1.x == -1 || g2.x == -1 || g3.x == -1 || g4.x == -1 ||
				o1.x == -1 || o2.x == -1 || o3.x == -1 || o4.x == -1) {
			return null;
		}
		final Polygon g = new Polygon(new int[]{g1.x, g2.x, g3.x, g4.x}, new int[]{g1.y, g2.y, g3.y, g4.y}, 4);
		final Polygon o = new Polygon(new int[]{o1.x, o2.x, o3.x, o4.x}, new int[]{o1.y, o2.y, o3.y, o4.y}, 4);
		final Polygon f1 = new Polygon(new int[]{g1.x, g2.x, o2.x, o1.x}, new int[]{g1.y, g2.y, o2.y, o2.y}, 4);
		final Polygon f2 = new Polygon(new int[]{g2.x, g3.x, o3.x, o2.x}, new int[]{g2.y, g3.y, o3.y, o2.y}, 4);
		final Polygon f3 = new Polygon(new int[]{g3.x, g4.x, o4.x, o3.x}, new int[]{g3.y, g4.y, o4.y, o3.y}, 4);
		final Polygon f4 = new Polygon(new int[]{g4.x, g1.x, o1.x, o4.x}, new int[]{g4.y, g1.y, o1.y, o4.y}, 4);
		final Area area = new Area();
		final Polygon[] arr = {g, o, f1, f2, f3, f4};
		for (final Polygon poly : arr) {
			area.add(new Area(poly));
		}
		return area;
	}

	@Override
	public Point getNextPoint() {
		final Area area = getCuboid(48), inner = getCuboid(32);
		if (area == null || inner == null) {
			return new Point(-1, -1);
		}
		final Rectangle r1 = area.getBounds(), r2 = inner.getBounds();
		return Calculations.nextPoint(r1, r2);
	}

	@Override
	public Point getCenterPoint() {
		final Area area = getCuboid(48);
		if (area != null) {
			final Rectangle rectangle = area.getBounds();
			return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
		}
		return new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final Area area = getCuboid(48);
		return area != null && area.contains(point);
	}

	@Override
	public boolean isValid() {
		return getCuboid(64) != null;
	}
}
