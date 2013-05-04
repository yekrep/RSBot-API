package org.powerbot.script.xenon;

import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.client.RSGroundBytes;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.Render;
import org.powerbot.client.RenderData;
import org.powerbot.client.TileData;
import org.powerbot.script.internal.Constants;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Locatable;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Tile;

public class Calculations {
	public static final int[] SIN_TABLE = new int[16384];
	public static final int[] COS_TABLE = new int[16384];
	public static final Toolkit toolkit;
	public static final Viewport viewport;

	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			Calculations.SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			Calculations.COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}

		toolkit = new Toolkit();
		viewport = new Viewport();
	}

	public static boolean isPointOnScreen(final Point point) {
		return isPointOnScreen(point.x, point.y);
	}

	public static boolean isPointOnScreen(final int x, final int y) {
		return true;//TODO
	}

	public static int tileHeight(final int x, final int y) {
		return tileHeight(x, y, -1);
	}

	public static int tileHeight(int x, int y, int plane) {
		final Client client = Bot.client();
		if (client == null) return 0;
		if (plane == -1) plane = client.getPlane();

		final RSInfo info = client.getRSGroundInfo();
		final RSGroundBytes groundBytes = info != null ? info.getGroundBytes() : null;
		final byte[][][] settings = groundBytes != null ? groundBytes.getBytes() : null;
		if (settings != null) {
			if ((x >= 512 && x <= 52224) || (y >= 512 && y <= 52224)) {
				x = x >> 9;
				y = y >> 9;
			}

			if (x < 0 || x > 103 || y < 0 || y > 103) return 0;
			if (plane <= 3 && (settings[1][x][y] & 2) != 0) {
				++plane;
			}
			final RSGroundInfo groundInfo = info.getRSGroundInfo();
			final TileData[] tileData = groundInfo != null ? groundInfo.getTileData() : null;
			if (tileData == null || plane < 0 || plane >= tileData.length) return 0;
			final int[][] heights = tileData[plane].getHeights();
			if (heights != null) {
				final int aX = x & 512 - 1;
				final int aY = y & 512 - 1;
				final int start_h = heights[x][y] * (512 - aX) + heights[x + 1][y] * aX >> 9;
				final int end_h = heights[x][1 + y] * (512 - aX) + heights[x + 1][y + 1] * aX >> 9;
				return start_h * (512 - aY) + end_h * aY >> 9;
			}
		}

		return 0;
	}

	public static Point groundToScreen(final int x, final int y, final int plane, final int height) {
		if (x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int h = tileHeight(x, y, plane) + height;
		return worldToScreen(x, h, y);
	}

	public static Point worldToScreen(int x, final int y, final int z) {
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

	public static Point worldToMap(double x, double y) {
		final Client client = Bot.client();
		if (client == null) return null;
		final Tile base = Game.getMapBase();
		final Player player = Players.getLocal();
		Tile loc;
		if (base == null || player == null || (loc = player.getLocation()) == null) return null;
		x -= base.x;
		y -= base.y;
		loc = loc.derive(-base.x, -base.y);
		final int pX = (int) (x * 4 + 2) - (loc.getX() << 9) / 128;
		final int pY = (int) (y * 4 + 2) - (loc.getY() << 9) / 128;
		final Component mapComponent = Components.getMap();
		if (mapComponent == null) return new Point(-1, -1);
		final int dist = pX * pX + pY * pY;
		final int mapRadius = Math.max(mapComponent.getWidth() / 2, mapComponent.getHeight() / 2) - 8;
		if (mapRadius * mapRadius >= dist) {
			final Constants constants = Bot.constants();
			final int SETTINGS_ON = constants != null ? constants.MINIMAP_SETTINGS_ON : -1;
			int angle = 0x3fff & (int) client.getMinimapAngle();
			final boolean unknown = client.getMinimapSettings() == SETTINGS_ON;
			if (!unknown) angle = 0x3fff & client.getMinimapOffset() + (int) client.getMinimapAngle();
			int sin = Calculations.SIN_TABLE[angle];
			int cos = Calculations.COS_TABLE[angle];
			if (!unknown) {
				final int fact = 0x100 + client.getMinimapScale();
				sin = 0x100 * sin / fact;
				cos = 0x100 * cos / fact;
			}
			final int _x = cos * pX + sin * pY >> 0xf;
			final int _y = cos * pY - sin * pX >> 0xf;
			final Point basePoint = mapComponent.getAbsoluteLocation();
			final int screen_x = _x + (int) basePoint.getX() + mapComponent.getWidth() / 2;
			final int screen_y = -_y + (int) basePoint.getY() + mapComponent.getHeight() / 2;
			return new Point(screen_x, screen_y);
		}
		return new Point(-1, -1);
	}

	public static double distance(final int x1, final int y1, final int x2, final int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public static double distance(final Locatable a, final Locatable b) {
		final Tile tA = a != null ? a.getLocation() : null, tB = b != null ? b.getLocation() : null;
		if (tA == null || tB == null) return Double.MAX_VALUE;
		return distance(tA.x, tA.y, tB.x, tB.y);
	}

	public static double distanceTo(final int x, final int y) {
		final Player local = Players.getLocal();
		final Tile location;
		if (local == null || (location = local.getLocation()) == null) return Double.MAX_VALUE;
		return distance(location.x, location.y, x, y);
	}

	public static double distanceTo(final Locatable locatable) {
		return distance(Players.getLocal(), locatable);
	}

	public static void updateToolkit(final Render render) {
		if (render == null) return;
		toolkit.absoluteX = render.getAbsoluteX();
		toolkit.absoluteY = render.getAbsoluteY();
		toolkit.xMultiplier = render.getXMultiplier();
		toolkit.yMultiplier = render.getYMultiplier();
		toolkit.graphicsIndex = render.getGraphicsIndex();

		final Constants constants = Bot.constants();
		final RenderData _viewport = render.getRenderData();
		final float[] data;
		if (viewport == null || constants == null || (data = _viewport.getFloats()) == null) return;
		viewport.xOff = data[constants.VIEWPORT_XOFF];
		viewport.xX = data[constants.VIEWPORT_XX];
		viewport.xY = data[constants.VIEWPORT_XY];
		viewport.xZ = data[constants.VIEWPORT_XZ];

		viewport.yOff = data[constants.VIEWPORT_YOFF];
		viewport.yX = data[constants.VIEWPORT_YX];
		viewport.yY = data[constants.VIEWPORT_YY];
		viewport.yZ = data[constants.VIEWPORT_YZ];

		viewport.zOff = data[constants.VIEWPORT_ZOFF];
		viewport.zX = data[constants.VIEWPORT_ZX];
		viewport.zY = data[constants.VIEWPORT_ZY];
		viewport.zZ = data[constants.VIEWPORT_ZZ];
	}

	public static class Toolkit {
		public float absoluteX, absoluteY;
		public float xMultiplier, yMultiplier;
		public int graphicsIndex;
	}

	public static class Viewport {
		public float xOff, xX, xY, xZ;
		public float yOff, yX, yY, yZ;
		public float zOff, zX, zY, zZ;
	}
}
