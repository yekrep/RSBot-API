package org.powerbot.game.api.wrappers;

import java.awt.Point;

import org.powerbot.game.api.methods.Calculations;

public class LocalTile extends Tile {
	public LocalTile(final int x, final int y, final int plane) {
		super(x, y, plane);
	}

	@Override
	public boolean verify() {
		return x > 0 && x < 104 && y > 0 && y < 104;
	}

	@Override
	public Point getPoint(final double xOff, final double yOff, final int height) {
		return Calculations.groundToScreen((int) ((x + xOff) * 0x200), (int) ((y + yOff) * 0x200), plane, height);
	}
}
