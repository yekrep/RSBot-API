package org.powerbot.game.api.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.Random;

/**
 * @author Timer
 */
public class Tile implements Entity {
	public final int x, y, plane;

	public Tile(final int x, final int y, final int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

	public Tile derive(final int x, final int y) {
		return new Tile(this.x + x, this.y + y, this.plane);
	}

	public boolean verify() {
		return false;//TODO
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
		return false;//TODO
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
		return Calculations.groundToScreen((int) ((x - Game.getBaseX() + xOff) * 0x200), (int) ((y - Game.getBaseY() + yOff) * 0x200), plane, height);
	}

	public void draw(Graphics render) {
		//TODO
	}
}
