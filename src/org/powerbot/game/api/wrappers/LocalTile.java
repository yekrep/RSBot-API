package org.powerbot.game.api.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.Random;

/**
 * @author Timer
 */
public class LocalTile implements Entity {
	public final int x, y, plane;

	public LocalTile(final int x, final int y, final int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public LocalTile derive(final int x, final int y) {
		return new LocalTile(this.x + x, this.y + y, this.plane);
	}

	public boolean verify() {
		return x > 0 && x < 104 && y > 0 && y < 104;
	}

	public Point getCentralPoint() {
		return getPoint(0.5d, 0.5d, 0);
	}

	public Point getNextViewportPoint() {
		return getPoint(Random.nextDouble(), Random.nextDouble(), 0);
	}

	public boolean contains(Point point) {
		return false;//TODO
	}

	public boolean isOnScreen() {
		final Point nw = getPoint(0.0d, 0.0d, 0);
		final Point ne = getPoint(1.0d, 0.0d, 0);
		final Point sw = getPoint(0.0d, 1.0d, 0);
		final Point se = getPoint(1.0d, 1.0d, 0);
		return nw.x != -1 || ne.x != -1 || sw.x != -1 || se.x != -1;
	}

	public Polygon[] getBounds() {
		return new Polygon[0];//TODO
	}

	public boolean hover() {
		return false;//TODO
	}

	public boolean click(boolean left) {
		return false;//TODO
	}

	public boolean interact(String action) {
		return false;//TODO
	}

	public boolean interact(String action, String option) {
		return false;//TODO
	}

	public Point getPoint(final double xOff, final double yOff, final int height) {
		return Calculations.groundToScreen((int) ((x + xOff) * 0x200), (int) ((y + yOff) * 0x200), plane, height);
	}

	public void draw(Graphics render) {
		//TODO
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(x).append(", ").append(y).append(')').toString();
	}
}