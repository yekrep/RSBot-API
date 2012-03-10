package org.powerbot.game.api.wrappers;

import java.awt.Point;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.Random;

public class Tile implements Locatable {
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

	public Point getCenterPoint() {
		return getPoint(0.5d, 0.5d);
	}

	public Point getNextPoint() {
		return getPoint(Random.nextDouble(), Random.nextDouble());
	}

	private Point getPoint(final double offX, final double offY) {
		return Calculations.groundToScreen((int) ((x - Game.getBaseX() + offX) * 512d), (int) ((y - Game.getBaseY() + offY) * 512d), plane, 0);
	}
}
