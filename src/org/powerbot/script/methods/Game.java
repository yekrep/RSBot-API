package org.powerbot.script.methods;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.Bot;
import org.powerbot.bot.World;
import org.powerbot.client.BaseInfo;
import org.powerbot.client.Client;
import org.powerbot.client.Constants;
import org.powerbot.client.HardReference;
import org.powerbot.client.HashTable;
import org.powerbot.client.Node;
import org.powerbot.client.RSGroundBytes;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.Render;
import org.powerbot.client.RenderData;
import org.powerbot.client.SoftReference;
import org.powerbot.client.TileData;
import org.powerbot.script.methods.widgets.ActionBar;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class Game {
	public static final int TAB_NONE = -1;
	public static final int TAB_COMBAT = 0;
	public static final int TAB_NOTICEBOARD = 1;
	public static final int TAB_STATS = 2;
	public static final int TAB_ACADEMY = 3;
	public static final int TAB_INVENTORY = 4;
	public static final int TAB_EQUIPMENT = 5;
	public static final int TAB_PRAYER = 6;
	public static final int TAB_ABILITY_BOOK = 7;
	public static final int TAB_EXTRAS = 8;
	public static final int TAB_FRIENDS = 9;
	public static final int TAB_FRIENDS_CHAT = 10;
	public static final int TAB_CLAN_CHAT = 11;
	public static final int TAB_OPTIONS = 12;
	public static final int TAB_EMOTES = 13;
	public static final int TAB_MUSIC = 14;
	public static final int TAB_NOTES = 15;
	public static final int TAB_LOGOUT = 16;
	public static final String[] TAB_NAMES = {
			"Combat", "Noticeboard", "Stats", "Combat Academy", "Inventory", "Worn Equipment", "Prayer List", "Ability Book",
			"Extras", "Friends List", "Friends Chat", "Clan Chat", "Options", "Emotes", "Music Player", "Notes", "Exit"
	};
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_LOGGING_IN = 9;
	public static final int INDEX_MAP_LOADED = 11;
	public static final int INDEX_MAP_LOADING = 12;
	public static final int[] INDEX_LOGGED_IN = {INDEX_MAP_LOADED, INDEX_MAP_LOADING};
	public static final int[] SIN_TABLE = new int[16384];
	public static final int[] COS_TABLE = new int[16384];

	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	public static int getCurrentTab() {
		Component c;
		for (int i = 0; i < TAB_NAMES.length - 1; i++) {
			if ((c = Components.getTab(i)) != null) {
				if (c.getTextureId() != -1) return i;
			}
		}
		if ((c = Widgets.get(182, 1)) != null && c.isVisible()) return TAB_LOGOUT;
		return TAB_NONE;
	}

	public static boolean openTab(final int index) {
		if (index < 0 || index >= TAB_NAMES.length) return false;
		if (getCurrentTab() == index) return true;
		final Component c = Components.getTab(index);
		if (c != null && c.isValid() && c.click(true)) {
			final Timer t = new Timer(800);
			while (t.isRunning() && getCurrentTab() != index) Delay.sleep(15);
		}
		return getCurrentTab() == index;
	}

	public static boolean closeTab() {
		if (isFixed()) return false;
		final int curr;
		if ((curr = getCurrentTab()) == TAB_NONE) return true;
		final Component c = Components.getTab(curr);
		if (c != null && c.isValid() && c.click(true)) {
			final Timer t = new Timer(800);
			while (t.isRunning() && getCurrentTab() != TAB_NONE) Delay.sleep(15);
		}
		return getCurrentTab() == TAB_NONE;
	}

	public static int getClientState() {
		final Client client = World.getWorld().getClient();
		if (client == null) return -1;

		final Constants constants = Bot.constants();
		final int state = client.getLoginIndex();
		if (state == constants.CLIENTSTATE_3) {
			return 3;
		} else if (state == constants.CLIENTSTATE_7) {
			return 7;
		} else if (state == constants.CLIENTSTATE_9) {
			return 9;
		} else if (state == constants.CLIENTSTATE_11) {
			return 11;
		} else if (state == constants.CLIENTSTATE_12) {
			return 12;
		}
		return -1;
	}

	public static boolean isLoggedIn() {
		final int curr = getClientState();
		for (final int s : INDEX_LOGGED_IN) if (s == curr) return true;
		return false;
	}

	public static Tile getMapBase() {
		final Client client = World.getWorld().getClient();
		if (client == null) return null;

		final RSInfo info = client.getRSGroundInfo();
		final BaseInfo baseInfo = info != null ? info.getBaseInfo() : null;
		return baseInfo != null ? new Tile(baseInfo.getX(), baseInfo.getY(), client.getPlane()) : null;
	}

	public static int getPlane() {
		final Client client = World.getWorld().getClient();
		if (client == null) return -1;
		return client.getPlane();
	}

	public static boolean isFixed() {
		final Client client = World.getWorld().getClient();
		if (client == null) return false;
		return client.getGUIRSInterfaceIndex() != 746;
	}

	public static void setPreferredWorld(final int world) {
		Bot.getInstance().preferredWorld = world;
	}

	public static Dimension getDimensions() {
		final Client client = World.getWorld().getClient();
		final Canvas canvas;
		if (client == null || (canvas = client.getCanvas()) == null) return new Dimension(0, 0);
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public static boolean isPointOnScreen(final Point point) {
		return isPointOnScreen(point.x, point.y);
	}

	public static boolean isPointOnScreen(final int x, final int y) {
		final Rectangle r;
		if (isLoggedIn()) {
			final Component c = Widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_BAR);
			r = c != null && c.isVisible() ? c.getBoundingRect() : null;
			if (r != null && r.contains(x, y)) return false;
			if (isFixed()) return x >= 4 && y >= 54 && x < 516 && y < 388;
		} else r = null;
		return true;
	}

	public static int tileHeight(int rX, int rY, int plane) {
		Client client = World.getWorld().getClient();
		if (client == null || plane < 0 || plane > 3) return 0;

		RSInfo world = client.getRSGroundInfo();
		RSGroundBytes ground = world != null ? world.getGroundBytes() : null;
		byte[][][] settings = ground != null ? ground.getBytes() : null;
		if (settings != null) {
			int x = rX >> 9, y = rY >> 9;
			if (x < 0 || x > 103 || y < 0 || y > 103) return 0;
			if (plane < 3 && (settings[1][x][y] & 2) != 0) {
				++plane;
			}
			RSGroundInfo worldGround = world.getRSGroundInfo();
			TileData[] groundPlanes = worldGround != null ? worldGround.getTileData() : null;
			if (groundPlanes == null || plane < 0 || plane >= groundPlanes.length) return 0;
			TileData groundData = groundPlanes[plane];
			if (groundData == null) return 0;
			int[][] heights = groundData.getHeights();
			if (heights != null) {
				int aX = rX & 0x1ff;
				int aY = rY & 0x1ff;
				int start_h = heights[x][y] * (512 - aX) + heights[x + 1][y] * aX >> 9;
				int end_h = heights[x][1 + y] * (512 - aX) + heights[x + 1][y + 1] * aX >> 9;
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
		final World world = World.getWorld();
		final Viewport viewport = world.getViewport();
		final Toolkit toolkit = world.getToolkit();
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
		final Client client = World.getWorld().getClient();
		if (client == null) return null;
		final Tile base = getMapBase();
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
			int sin = SIN_TABLE[angle];
			int cos = COS_TABLE[angle];
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

	public static void updateToolkit(final Render render) {
		if (render == null) return;
		final World world = World.getWorld();
		final Viewport viewport = world.getViewport();
		final Toolkit toolkit = world.getToolkit();
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

	public static Object lookup(final HashTable nc, final long id) {
		final Node[] buckets;
		if (nc == null || (buckets = nc.getBuckets()) == null || id < 0) {
			return null;
		}

		final Node n = buckets[(int) (id & buckets.length - 1)];
		for (Node node = n.getNext(); node != n && node != null; node = node.getNext()) {
			if (node.getId() == id) {
				if (node instanceof SoftReference) {
					return ((java.lang.ref.SoftReference<?>) ((SoftReference) node).get()).get();
				} else if (node instanceof HardReference) {
					return ((HardReference) node).get();
				} else {
					return node;
				}
			}
		}
		return null;
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
