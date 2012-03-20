package org.powerbot.game.api.methods;

import java.awt.Canvas;
import java.awt.Point;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSGroundBytes_Bytes;
import org.powerbot.game.client.RSGroundInfoTileData;
import org.powerbot.game.client.RSInfoGroundBytes;
import org.powerbot.game.client.RSInfoRSGroundInfo;
import org.powerbot.game.client.TileData;

/**
 * A utility for the manipulation of different calculations for the game.
 *
 * @author Timer
 */
public class Calculations {
	/**
	 * A representation of the game's (Java) Toolkit.
	 *
	 * @author Timer
	 */
	public static class Toolkit {
		public float absoluteX, absoluteY;
		public float xMultiplier, yMultiplier;
	}

	/**
	 * A representation of the game's Viewport, or Matrix.
	 *
	 * @author Timer
	 */
	public static class Viewport {
		public float xOff, xX, xY, xZ;
		public float yOff, yX, yY, yZ;
		public float zOff, zX, zY, zZ;
	}

	public static final int[] SIN_TABLE = new int[16384];
	public static final int[] COS_TABLE = new int[16384];

	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			Calculations.SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			Calculations.COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	/**
	 * @param x     The local x position of the tile of which you desire to get the height for.
	 * @param y     The local y position of the tile of which you desire to get the height for.
	 * @param plane The plane to access this tile's information on.
	 * @return The height of the given tile on the provided plane.
	 */
	public static int calculateTileHeight(final int x, final int y, int plane) {
		final Client client = Bot.resolve().client;
		final int x1 = x >> 9;
		final int y1 = y >> 9;
		final byte[][][] settings = (byte[][][]) ((RSGroundBytes_Bytes) (((RSInfoGroundBytes) client.getRSGroundInfo()).getRSInfoGroundBytes())).getRSGroundBytes_Bytes();
		if (settings != null && x1 >= 0 && x1 < 104 && y1 >= 0 && y1 < 104) {
			if (plane <= 3 && (settings[1][x1][y1] & 2) != 0) {
				++plane;
			}
			final TileData[] planes = (TileData[]) ((RSGroundInfoTileData) ((RSInfoRSGroundInfo) client.getRSGroundInfo()).getRSInfoRSGroundInfo()).getRSGroundInfoTileData();
			if (planes != null && plane < planes.length && planes[plane] != null) {
				final int[][] heights = planes[plane].getHeights();
				if (heights != null) {
					final int x2 = x & 512 - 1;
					final int y2 = y & 512 - 1;
					final int start_h = heights[x1][y1] * (512 - x2) + heights[x1 + 1][y1] * x2 >> 9;
					final int end_h = heights[x1][1 + y1] * (512 - x2) + heights[x1 + 1][y1 + 1] * x2 >> 9;
					return start_h * (512 - y2) + end_h * y2 >> 9;
				}
			}
		}
		return 0;
	}

	/**
	 * @param x      The absolute x ground position.
	 * @param y      The absolute x ground position.
	 * @param plane  The plane to calculation this tile's position on.
	 * @param height The height offset.
	 * @return The <code>Point</code> of the given tile on the screen.
	 */
	public static Point groundToScreen(final int x, final int y, final int plane, final int height) {
		if (x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int z = calculateTileHeight(x, y, plane) - height;
		return worldToScreen(x, z, y);
	}

	/**
	 * @param x Absolute x position of the calculation.
	 * @param y Depth of the requested calculation.
	 * @param z Absolute y position of the calculation.
	 * @return The <code>Point</code> of the given coordinates on screen.
	 */
	public static Point worldToScreen(final int x, final int y, final int z) {
		final Bot bot = Bot.resolve();
		final Toolkit toolkit = bot.toolkit;
		final Viewport viewport = bot.viewport;
		final float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
		final float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
		final float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yZ * z));
		if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
			return new Point(
					Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z),
					Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z)
			);
		}
		return new Point(-1, -1);
	}

	/**
	 * @param point The <code>Point</code> to determine if it's on screen or not.
	 * @return <tt>true</tt> if the point is on the screen; otherwise <tt>false</tt>.
	 */
	public static boolean isPointOnScreen(final Point point) {
		final Canvas canvas = Bot.resolve().getCanvas();//TODO
		return point.x > 0 && point.y > 0 && point.x < canvas.getWidth() && point.y < canvas.getHeight();
	}
}
