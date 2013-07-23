package org.powerbot.script.methods;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.Bot;
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
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class Game extends MethodProvider {
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

	public final Game.Toolkit toolkit;
	public final Game.Viewport viewport;

	public int mapAngle;

	public Game(MethodContext factory) {
		super(factory);
		this.toolkit = new Toolkit();
		this.viewport = new Viewport();
	}

	public int getClientState() {
		Client client = ctx.getClient();
		final Constants constants = getConstants();
		if (client == null || constants == null) {
			return -1;
		}
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

	public boolean isLoggedIn() {
		final int curr = getClientState();
		for (final int s : INDEX_LOGGED_IN) {
			if (s == curr) {
				return true;
			}
		}
		return false;
	}

	public Tile getMapBase() {
		Client client = ctx.getClient();
		if (client == null) {
			return Tile.NIL;
		}

		final RSInfo info = client.getRSGroundInfo();
		final BaseInfo baseInfo = info != null ? info.getBaseInfo() : null;
		return baseInfo != null ? new Tile(baseInfo.getX(), baseInfo.getY(), client.getPlane()) : Tile.NIL;
	}

	public int getPlane() {
		Client client = ctx.getClient();
		if (client == null) {
			return -1;
		}
		return client.getPlane();
	}

	public boolean isFixed() {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		return client.getGUIRSInterfaceIndex() != 746;
	}

	public void setPreferredWorld(final int world) {
		ctx.setPreferredWorld(world);
	}

	public int getPreferredWorld() {
		return ctx.getPreferredWorld();
	}

	public Dimension getDimensions() {
		Client client = ctx.getClient();
		final Canvas canvas;
		if (client == null || (canvas = client.getCanvas()) == null) {
			return new Dimension(0, 0);
		}
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public boolean isPointOnScreen(final Point point) {
		return isPointOnScreen(point.x, point.y);
	}

	public boolean isPointOnScreen(final int x, final int y) {
		final Rectangle r;
		if (isLoggedIn()) {
			/*final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_BAR);
			r = c != null && c.isVisible() ? c.getBoundingRect() : null;
			if (r != null && r.contains(x, y)) {
				return false;
			}
			if (isFixed()) {
				return x >= 4 && y >= 54 && x < 516 && y < 388;
			}*/
			//TODO this
			Dimension dimension = ctx.game.getDimensions();
			return x > 0 && y > 0 && x < dimension.getWidth() && y < dimension.getHeight();
		} else {
			r = null;
		}
		return true;
	}

	public int tileHeight(final int rX, final int rY, int plane) {
		Client client = ctx.getClient();
		if (client == null) {
			return 0;
		}
		if (plane == -1) {
			plane = client.getPlane();
		}
		RSInfo world = client.getRSGroundInfo();
		RSGroundBytes ground = world != null ? world.getGroundBytes() : null;
		byte[][][] settings = ground != null ? ground.getBytes() : null;
		if (settings != null) {
			int x = rX >> 9, y = rY >> 9;
			if (x < 0 || x > 103 || y < 0 || y > 103) {
				return 0;
			}
			if (plane < 3 && (settings[1][x][y] & 2) != 0) {
				++plane;
			}
			RSGroundInfo worldGround = world.getRSGroundInfo();
			TileData[] groundPlanes = worldGround != null ? worldGround.getTileData() : null;
			if (groundPlanes == null || plane < 0 || plane >= groundPlanes.length) {
				return 0;
			}
			TileData groundData = groundPlanes[plane];
			if (groundData == null) {
				return 0;
			}
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

	public Point groundToScreen(final int x, final int y, final int plane, final int height) {
		if (x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int h = tileHeight(x, y, plane) + height;
		return worldToScreen(x, h, y);
	}

	public Point worldToScreen(int x, final int y, final int z) {
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

	public Point tileToMap(double x, double y) {
		Client client = ctx.getClient();
		if (client == null) {
			return new Point(-1, -1);
		}
		final Tile base = getMapBase();
		final Player player = ctx.players.getLocal();
		Tile loc;
		if (base == null || player == null || (loc = player.getLocation()) == null) {
			return new Point(-1, -1);
		}
		x -= base.x;
		y -= base.y;
		loc = loc.derive(-base.x, -base.y);
		final int pX = (int) (x * 4 + 2) - (loc.getX() << 9) / 128;
		final int pY = (int) (y * 4 + 2) - (loc.getY() << 9) / 128;
		final Component mapComponent = ctx.widgets.get(1477, 53);//TODO: this
		if (mapComponent == null) {
			return new Point(-1, -1);
		}
		final int dist = pX * pX + pY * pY;
		final int mapRadius = Math.max(mapComponent.getWidth() / 2, mapComponent.getHeight() / 2) - 8;
		if (mapRadius * mapRadius >= dist) {
			final Constants constants = getConstants();
			final int SETTINGS_ON = constants != null ? constants.MINIMAP_SETTINGS_ON : -1;
			final boolean flag = client.getMinimapSettings() == SETTINGS_ON;
			int sin = SIN_TABLE[mapAngle];
			int cos = COS_TABLE[mapAngle];
			if (!flag) {
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

	public void updateToolkit(final Render render) {
		if (render == null) {
			return;
		}
		toolkit.absoluteX = render.getAbsoluteX();
		toolkit.absoluteY = render.getAbsoluteY();
		toolkit.xMultiplier = render.getXMultiplier();
		toolkit.yMultiplier = render.getYMultiplier();
		toolkit.graphicsIndex = render.getGraphicsIndex();

		final Constants constants = getConstants();
		final RenderData _viewport = render.getRenderData();
		final float[] data;
		if (viewport == null || constants == null || (data = _viewport.getFloats()) == null) {
			return;
		}
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

	public Object lookup(final HashTable nc, final long id) {
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

	private Constants getConstants() {
		Bot bot = ctx.getBot();
		return bot != null ? bot.getConstants() : null;
	}

	public class Toolkit {
		public float absoluteX, absoluteY;
		public float xMultiplier, yMultiplier;
		public int graphicsIndex;
	}

	public class Viewport {
		public float xOff, xX, xY, xZ;
		public float yOff, yX, yY, yZ;
		public float zOff, zX, zY, zZ;
	}
}
