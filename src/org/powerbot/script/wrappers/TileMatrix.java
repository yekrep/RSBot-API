package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

/**
 * An interactive tile matrix.
 */
public final class TileMatrix extends Interactive implements Locatable, Drawable {
	private final Tile tile;

	TileMatrix(final MethodContext ctx, final Tile tile) {
		super(ctx);
		this.tile = tile;
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int getX() {
				final Tile base = ctx.game.getMapBase();
				return ((tile.x - base.x) * 512) + 256;
			}

			@Override
			public int getZ() {
				final Tile base = ctx.game.getMapBase();
				return ((tile.y - base.y) * 512) + 256;
			}
		});
	}

	public Point getPoint(final int height) {
		return getPoint(0.5d, 0.5d, height);
	}

	public Point getPoint(final double modX, final double modY, final int height) {
		final Tile base = ctx.game.getMapBase();
		return base != null ? ctx.game.groundToScreen((int) ((tile.x - base.x + modX) * 512d), (int) ((tile.y - base.y + modY) * 512d), tile.plane, height) : new Point(-1, -1);
	}

	public Polygon getBounds() {
		final Point tl = getPoint(0.0D, 0.0D, 0);
		final Point tr = getPoint(1.0D, 0.0D, 0);
		final Point br = getPoint(1.0D, 1.0D, 0);
		final Point bl = getPoint(0.0D, 1.0D, 0);
		return new Polygon(
				new int[]{tl.x, tr.x, br.x, bl.x},
				new int[]{tl.y, tr.y, br.y, bl.y},
				4
		);
	}

	public Point getMapPoint() {
		return ctx.game.tileToMap(tile);
	}

	public boolean isOnMap() {
		final Point p = getMapPoint();
		return p.x != -1 && p.y != -1;
	}

	public boolean isReachable() {
		return ctx.movement.isReachable(ctx.players.local().getLocation(), tile);
	}

	@Override
	public Tile getLocation() {
		return tile;
	}

	@Override
	public boolean isInViewport() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return ctx.game.isPointInViewport(getInteractPoint());
		}
		return isPolygonInViewport(getBounds());
	}

	private boolean isPolygonInViewport(final Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			if (!ctx.game.isPointInViewport(p.xpoints[i], p.ypoints[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Point getInteractPoint() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		final int x = Random.nextGaussian(0, 100, 5);
		final int y = Random.nextGaussian(0, 100, 5);
		return getPoint(x / 100.0D, y / 100.0D, 0);
	}

	@Override
	public Point getNextPoint() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		return getPoint(Random.nextDouble(0.0D, 1.0D), Random.nextDouble(0.0D, 1.0D), 0);
	}

	@Override
	public Point getCenterPoint() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getCenterPoint();
		}
		return getPoint(0);
	}

	@Override
	public boolean contains(final Point point) {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.contains(point);
		}
		final Polygon p = getBounds();
		return isPolygonInViewport(p) && p.contains(point);
	}

	@Override
	public boolean isValid() {
		final Tile t = ctx.game.getMapBase();
		if (t == null) {
			return false;
		}
		final int x = tile.x - t.x, y = tile.y - t.y;
		return x >= 0 && y >= 0 && x < 104 && y < 104;
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 75);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		final Polygon p = getBounds();
		if (!isPolygonInViewport(p)) {
			return;
		}

		Color c = Tile.TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final BoundingModel m2 = boundingModel.get();
		if (m2 != null) {
			m2.drawWireFrame(render);
			return;
		}
		render.drawPolygon(p);
		render.setColor(new Color(0, 0, 0, 20));
		render.fillPolygon(p);
	}

	@Override
	public String toString() {
		return tile.toString();
	}
}
