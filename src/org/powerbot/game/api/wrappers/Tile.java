package org.powerbot.game.api.wrappers;

import java.awt.Point;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;

/**
 * @author Timer
 */
public class Tile extends LocalTile {
	public Tile(final int x, final int y, final int plane) {
		super(x, y, plane);
	}

	@Override
	public boolean verify() {
		final int x = this.x - Game.getBaseX();
		final int y = this.y - Game.getBaseY();
		return x > 0 && x < 104 && y > 0 && y < 104;
	}

	@Override
	public Point getPoint(final double xOff, final double yOff, final int height) {
		return Calculations.groundToScreen((int) ((x - Game.getBaseX() + xOff) * 0x200), (int) ((y - Game.getBaseY() + yOff) * 0x200), plane, height);
	}
}
