package org.powerbot.script.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.lang.ref.WeakReference;

import org.powerbot.client.RSAnimable;
import org.powerbot.client.RSInteractable;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class RenderableCuboid extends Interactive {
	private final WeakReference<RSInteractable> interactable;

	public RenderableCuboid(final MethodContext ctx, final RSInteractable interactable) {
		super(ctx);
		this.interactable = new WeakReference<RSInteractable>(interactable);
	}

	private Area cuboid() {
		final RSInteractable interactable = this.interactable.get();
		final RSInteractableData data;
		final RSInteractableLocation location;
		if (interactable == null || (data = interactable.getData()) == null ||
				(location = data.getLocation()) == null) {
			return null;
		}

		final int x = Math.round(location.getX()), z = Math.round(location.getY()),
				y = Math.round(location.getZ()), p = interactable.getPlane();
		final int h;
		if (interactable instanceof RSCharacter) {
			final RSCharacter c = (RSCharacter) interactable;
			h = c.getHeight();
		} else {
			h = 0;
		}
		if (ctx.game.groundToScreen(x, z, p, h / 2).x == -1) {
			return null;
		}
		int ldx = 256, rdx = 256;
		int ldz = 256, rdz = 256;
		if (interactable instanceof RSAnimable) {
			final int tx = x >> 9, ty = z >> 9;
			final RSAnimable animable = (RSAnimable) interactable;
			final int x1 = animable.getX1(), y1 = animable.getY1();
			final int x2 = animable.getX2(), y2 = animable.getY2();
			int b = Math.abs(x1 - tx);
			ldx += b * 512;
			b = Math.abs(x2 - tx);
			rdx += b * 512;
			b = Math.abs(y1 - ty);
			ldz += b * 512;
			b = Math.abs(y2 - ty);
			rdz += b * 512;
		}

		final Point g1 = ctx.game.groundToScreen(x - ldx, z - ldz, p, 0);
		final Point g2 = ctx.game.groundToScreen(x + rdx, z - ldz, p, 0);
		final Point g3 = ctx.game.groundToScreen(x + rdx, z + rdz, p, 0);
		final Point g4 = ctx.game.groundToScreen(x - ldx, z + rdz, p, 0);
		final Point o1 = ctx.game.groundToScreen(x - ldx, z - ldz, p, h);
		final Point o2 = ctx.game.groundToScreen(x + rdx, z - ldz, p, h);
		final Point o3 = ctx.game.groundToScreen(x + rdx, z + rdz, p, h);
		final Point o4 = ctx.game.groundToScreen(x - ldx, z + rdz, p, h);
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
		final Area area = cuboid();
		final Rectangle rectangle;
		if (area != null && (rectangle = area.getBounds()).width > 1 && rectangle.height > 1) {
			final int x = rectangle.x, y = rectangle.y, w = rectangle.width, h = rectangle.height;
			Point p;
			do {
				p = new Point(x + Random.nextInt(0, w), y + Random.nextInt(0, h));
			} while (!area.contains(p));
			return p;
		}
		return new Point(-1, -1);
	}

	@Override
	public Point getCenterPoint() {
		final Area area = cuboid();
		if (area != null) {
			final Rectangle rectangle = area.getBounds();
			return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
		}
		return new Point(-1, -1);
	}

	@Override
	public boolean contains(Point point) {
		final Area area = cuboid();
		return area != null && area.contains(point);
	}

	@Override
	public boolean isValid() {
		return cuboid() != null;
	}
}
