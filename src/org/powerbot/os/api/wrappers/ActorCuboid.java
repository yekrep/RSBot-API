package org.powerbot.os.api.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.lang.ref.WeakReference;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.api.util.Calculations;
import org.powerbot.os.client.Actor;

public class ActorCuboid extends Interactive {
	private final WeakReference<org.powerbot.os.client.Actor> actor;

	public ActorCuboid(final MethodContext ctx, final Actor actor) {
		super(ctx);
		this.actor = new WeakReference<Actor>(actor);
	}

	public Area cuboid(final int deviation) {//TODO: private
		final Actor actor = this.actor.get();
		if (actor == null) {
			return null;
		}
		final int x = actor.getX(), z = actor.getZ();
		final int y = ctx.game.getHeight(x, z), h = actor.getHeight();
		if (ctx.game.worldToScreen(x, z, h / 2).x == -1) {
			return null;
		}
		final Point g1 = ctx.game.worldToScreen(x - deviation, y, z - deviation, 0);
		final Point g2 = ctx.game.worldToScreen(x + deviation, y, z - deviation, 0);
		final Point g3 = ctx.game.worldToScreen(x + deviation, y, z + deviation, 0);
		final Point g4 = ctx.game.worldToScreen(x - deviation, y, z + deviation, 0);
		final Point o1 = ctx.game.worldToScreen(x - deviation, y, z - deviation, h);
		final Point o2 = ctx.game.worldToScreen(x + deviation, y, z - deviation, h);
		final Point o3 = ctx.game.worldToScreen(x + deviation, y, z + deviation, h);
		final Point o4 = ctx.game.worldToScreen(x - deviation, y, z + deviation, h);
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
		for (Polygon poly : arr) {
			area.add(new Area(poly));
		}
		return area;
	}

	@Override
	public Point getInteractPoint() {
		return getNextPoint();
	}

	@Override
	public Point getNextPoint() {
		final Area area = cuboid(64), inner = cuboid(32);
		if (area == null || inner == null) {
			return new Point(-1, -1);
		}
		final Rectangle r1 = area.getBounds(), r2 = inner.getBounds();
		return Calculations.nextPoint(r1, r2);
	}

	@Override
	public Point getCenterPoint() {
		final Area area = cuboid(64);
		if (area != null) {
			final Rectangle rectangle = area.getBounds();
			return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
		}
		return new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final Area area = cuboid(64);
		return area != null && area.contains(point);
	}

	@Override
	public boolean isValid() {
		return cuboid(64) != null;
	}
}
