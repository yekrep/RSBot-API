package org.powerbot.script.rs3;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.lang.ref.WeakReference;

import org.powerbot.bot.rs3.client.RSCharacter;
import org.powerbot.bot.rs3.client.RSInteractable;
import org.powerbot.bot.rs3.client.RSInteractableData;
import org.powerbot.bot.rs3.client.RSInteractableLocation;
import org.powerbot.script.Calculations;

public class TileCuboid extends Interactive {
	private final WeakReference<RSInteractable> interactable;

	public TileCuboid(final ClientContext ctx, final RSInteractable interactable) {
		super(ctx);
		this.interactable = new WeakReference<RSInteractable>(interactable);
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
	}

	private Area cuboid(final int deviation) {
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
		final Point g1 = ctx.game.groundToScreen(x - deviation, z - deviation, p, 0);
		final Point g2 = ctx.game.groundToScreen(x + deviation, z - deviation, p, 0);
		final Point g3 = ctx.game.groundToScreen(x + deviation, z + deviation, p, 0);
		final Point g4 = ctx.game.groundToScreen(x - deviation, z + deviation, p, 0);
		final Point o1 = ctx.game.groundToScreen(x - deviation, z - deviation, p, h);
		final Point o2 = ctx.game.groundToScreen(x + deviation, z - deviation, p, h);
		final Point o3 = ctx.game.groundToScreen(x + deviation, z + deviation, p, h);
		final Point o4 = ctx.game.groundToScreen(x - deviation, z + deviation, p, h);
		if (!ctx.game.isPointInViewport(g1) || !ctx.game.isPointInViewport(g2) || !ctx.game.isPointInViewport(g3) || !ctx.game.isPointInViewport(g4) ||
				!ctx.game.isPointInViewport(o1) || !ctx.game.isPointInViewport(o2) || !ctx.game.isPointInViewport(o3) || !ctx.game.isPointInViewport(o4)) {
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
		final Area area = cuboid(256), inner = cuboid(128);
		if (area == null || inner == null) {
			return new Point(-1, -1);
		}
		final Rectangle r1 = area.getBounds(), r2 = inner.getBounds();
		return Calculations.nextPoint(r1, r2);
	}

	public Point getCenterPoint() {
		final Area area = cuboid(256);
		if (area != null) {
			final Rectangle rectangle = area.getBounds();
			return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
		}
		return new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final Area area = cuboid(256);
		return area != null && area.contains(point);
	}

	@Override
	public boolean isValid() {
		return cuboid(256) != null;
	}
}
