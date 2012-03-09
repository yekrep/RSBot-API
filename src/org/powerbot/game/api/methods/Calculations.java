package org.powerbot.game.api.methods;

import java.awt.Point;

import org.powerbot.game.bot.Bot;

public class Calculations {
	public static class Toolkit {
		public float absoluteX, absoluteY;
		public float xMultiplier, yMultiplier;
	}

	public static class Viewport {
		public float xOff, xX, xY, xZ;
		public float yOff, yX, yY, yZ;
		public float zOff, zX, zY, zZ;
	}

	public static Point worldToScreen(final int x, final int y, final int z) {
		final Bot bot = Bot.resolve();
		final Toolkit toolkit = bot.toolkit;
		final Viewport viewport = bot.viewport;
		float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
		float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
		float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yX * z));
		if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
			return new Point(
					Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z),
					Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z)
			);
		}
		return new Point(-1, -1);
	}
}
