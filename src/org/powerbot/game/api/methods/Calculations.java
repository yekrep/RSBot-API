package org.powerbot.game.api.methods;

import java.awt.Point;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSGroundBytes_Bytes;
import org.powerbot.game.client.RSGroundInfoTileData;
import org.powerbot.game.client.RSInfoGroundBytes;
import org.powerbot.game.client.RSInfoRSGroundInfo;
import org.powerbot.game.client.TileData;

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

	private static int calculateTileHeight(final int x, final int y, int plane) {
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

	public static Point groundToScreen(final int x, final int y, final int plane, final int height) {
		if (x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int z = calculateTileHeight(x, y, plane) + height;
		return worldToScreen(x, y, z);
	}

	public static Point worldToScreen(final int x, final int y, final int z) {
		final Bot bot = Bot.resolve();
		final Toolkit toolkit = bot.toolkit;
		final Viewport viewport = bot.viewport;
		final float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * z + viewport.zZ * y));
		final float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * z + viewport.xZ * y));
		final float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * z + viewport.yX * y));
		if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
			return new Point(
					Math.round((toolkit.xMultiplier * _x) / _z - toolkit.absoluteX),
					Math.round((toolkit.yMultiplier * _y) / _z - toolkit.absoluteY)
			);
		}
		return new Point(-1, -1);
	}
}
