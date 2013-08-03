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
	private Tile tile;

	TileMatrix(MethodContext ctx, Tile tile) {
		super(ctx);
		this.tile = tile;
	}

	public Point getPoint(final int height) {
		return getPoint(0.5d, 0.5d, height);
	}

	public Point getPoint(final double modX, final double modY, final int height) {
		final Tile base = ctx.game.getMapBase();
		return base != null ? ctx.game.groundToScreen((int) ((tile.x - base.x + modX) * 512d), (int) ((tile.y - base.y + modY) * 512d), tile.plane, height) : new Point(-1, -1);
	}

	public Polygon getBounds() {
		Point tl = getPoint(0.0D, 0.0D, 0);
		Point tr = getPoint(1.0D, 0.0D, 0);
		Point br = getPoint(1.0D, 1.0D, 0);
		Point bl = getPoint(0.0D, 1.0D, 0);
		return new Polygon(
				new int[]{tl.x, tr.x, br.x, bl.x},
				new int[]{tl.y, tr.y, br.y, bl.y},
				4
		);
	}

	public Point getMapPoint() {
		return ctx.game.tileToMap(tile.getX(), tile.getY());
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
	public boolean isOnScreen() {
		return isPolygonOnScreen(getBounds());
	}

	private boolean isPolygonOnScreen(Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			if (!ctx.game.isPointOnScreen(p.xpoints[i], p.ypoints[i])) {
				return false;
			}
		}
		return true;
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
		Polygon p = getBounds();
		return isPolygonOnScreen(p) && p.contains(point);
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
		Polygon p = getBounds();
		if (!isPolygonOnScreen(p)) {
			return;
		}

		Color c = Tile.TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		render.drawPolygon(p);
		render.setColor(new Color(0, 0, 0, 20));
		render.fillPolygon(p);
	}
}
