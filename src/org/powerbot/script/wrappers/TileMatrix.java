package org.powerbot.script.wrappers;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Random;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * An interactive tile matrix.
 */
public final class TileMatrix extends Interactive implements Locatable, Drawable {
	private Tile tile;

	TileMatrix(ClientFactory ctx, Tile tile) {
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

	public Point getMapPoint() {
		return ctx.game.worldToMap(tile.getX() + 0.5d, tile.getY() + 0.5d);
	}

	public boolean isOnMap() {
		final Point p = getMapPoint();
		return p.x != -1 && p.y != -1;
	}

	@Override
	public Tile getLocation() {
		return tile;
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
		final Point topLeft = getPoint(0.0D, 0.0D, 0);
		final Point topRight = getPoint(1.0D, 0.0D, 0);
		final Point bottomRight = getPoint(1.0D, 1.0D, 0);
		final Point bottomLeft = getPoint(0.0D, 1.0D, 0);
		if (ctx.game.isPointOnScreen(topLeft) && ctx.game.isPointOnScreen(topRight) &&
				ctx.game.isPointOnScreen(bottomRight) && ctx.game.isPointOnScreen(bottomLeft)) {
			final Polygon p = new Polygon();
			p.addPoint(topLeft.x, topLeft.y);
			p.addPoint(topRight.x, topRight.y);
			p.addPoint(bottomRight.x, bottomRight.y);
			p.addPoint(bottomLeft.x, bottomLeft.y);
			return p.contains(point);
		}
		return false;
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
		Color c = Tile.TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final Point topLeft = getPoint(0.0D, 0.0D, 0);
		final Point topRight = getPoint(1.0D, 0.0D, 0);
		final Point bottomRight = getPoint(1.0D, 1.0D, 0);
		final Point bottomLeft = getPoint(0.0D, 1.0D, 0);
		if (ctx.game.isPointOnScreen(topLeft) && ctx.game.isPointOnScreen(topRight) &&
				ctx.game.isPointOnScreen(bottomRight) && ctx.game.isPointOnScreen(bottomLeft)) {
			final Polygon p = new Polygon();
			p.addPoint(topLeft.x, topLeft.y);
			p.addPoint(topRight.x, topRight.y);
			p.addPoint(bottomRight.x, bottomRight.y);
			p.addPoint(bottomLeft.x, bottomLeft.y);
			render.drawPolygon(p);
			render.setColor(new Color(0, 0, 0, 20));
			render.fillPolygon(p);
		}
	}
}
