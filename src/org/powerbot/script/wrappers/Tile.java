package org.powerbot.script.wrappers;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Random;

import java.awt.*;

public class Tile extends Interactive implements Locatable, Drawable {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 75);
	public final int x;
	public final int y;
	public final int plane;

	public Tile(ClientFactory ctx, final int x, final int y) {
		this(ctx, x, y, 0);
	}

	public Tile(ClientFactory ctx, final int x, final int y, final int plane) {
		super(ctx);
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		return plane;
	}

	public Tile derive(final int x, final int y) {
		return derive(x, y, this.plane);
	}

	public Tile derive(final int x, final int y, final int plane) {
		return new Tile(ctx, this.x + x, this.y + y, plane);
	}

	public Tile randomize(final int left, final int right, final int down, final int up) {
		return derive(Random.nextInt(left, right + 1), Random.nextInt(down, up + 1));
	}

	public Tile randomize(final int x, final int y) {
		return randomize(-x, x, -y, y);
	}

	public Point getPoint(final int height) {
		return getPoint(0.5d, 0.5d, height);
	}

	public Point getPoint(final double modX, final double modY, final int height) {
		final Tile base = ctx.game.getMapBase();
		return base != null ? ctx.game.groundToScreen((int) ((x - base.x + modX) * 512d), (int) ((y - base.y + modY) * 512d), plane, height) : new Point(-1, -1);
	}

	public Point getMapPoint() {
		return ctx.game.worldToMap(getX() + 0.5d, getY() + 0.5d);
	}

	public boolean isOnMap() {
		final Point p = getMapPoint();
		return p.x != -1 && p.y != -1;
	}

	@Override
	public Tile getLocation() {
		return this;
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
		if (t == null) return false;
		final int x = this.x - t.x, y = this.y - t.y;
		return x >= 0 && y >= 0 && x < 104 && y < 104;
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 75);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		Color c = TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
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

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + plane + ')';
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Tile)) return false;
		final Tile t = (Tile) o;
		return x == t.x && y == t.y && plane == t.plane;
	}
}
