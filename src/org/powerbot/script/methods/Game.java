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
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.RelativeLocation;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.Widget;

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
		final double d = 0.0003834951969714103d;
		for (int i = 0; i < 16384; i++) {
			SIN_TABLE[i] = (int) (16384d * Math.sin(i * d));
			COS_TABLE[i] = (int) (16384d * Math.cos(i * d));
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

	public enum Crosshair {
		NONE, DEFAULT, ACTION
	}

	public boolean logout(boolean lobby) {
		if (ctx.widgets.get(1477, 75).getChild(1).interact("Logout")) {//TODO: auto detect
			Widget widget = ctx.widgets.get(26);
			for (int i = 0; i < 20; i++) {
				if (widget.isValid()) {
					break;
				}
				sleep(100, 200);
			}
			if (widget.isValid()) {
				if (widget.getComponent(lobby ? 18 : 11).interact("Select")) {
					for (int i = 0; i < 10; i++) {
						if (getClientState() == (lobby ? INDEX_LOBBY_SCREEN : INDEX_LOGIN_SCREEN)) {
							break;
						}
						sleep(700, 1000);
					}
				}
			}
		}
		return getClientState() == (lobby ? INDEX_LOBBY_SCREEN : INDEX_LOGIN_SCREEN);
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

	public Crosshair getCrosshair() {
		Client client = ctx.getClient();
		int type = client != null ? client.getCrossHairType() : -1;
		if (type < 0 || type > 2) {
			return Crosshair.NONE;
		}
		return Crosshair.values()[type];
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
		Dimension dimension = getDimensions();
		if (x > 0 && y > 0) {
			if (isLoggedIn()) {
				Rectangle[] rectangles = ctx.hud.getBounds();
				for (Rectangle rectangle : rectangles) {
					if (rectangle.contains(x, y)) {
						return false;
					}
				}
			}
			return x < dimension.getWidth() && y < dimension.getHeight();
		}
		return false;
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

	public Point tileToMap(Locatable locatable) {
		Tile tile = locatable.getLocation();
		Client client = ctx.getClient();
		RelativeLocation relative = ctx.players.local().getRelative();
		Tile base = getMapBase();
		tile = tile.derive(-base.getX(), -base.getY());
		if (client == null ||
				relative == RelativeLocation.NIL || tile == Tile.NIL) {
			return new Point(-1, -1);
		}
		int pX = (int) ((tile.getX() * 4 + 2) - relative.getX() / 128);
		int pY = (int) ((tile.getY() * 4 + 2) - relative.getY() / 128);
		Component component = ctx.widgets.get(1465, 12);
		int dist = pX * pX + pY * pY;
		int mapRadius = Math.max(component.getScrollWidth() / 2, component.getScrollHeight() / 2) + 10;
		if (dist > mapRadius * mapRadius) {
			return new Point(-1, -1);
		}
		Constants constants = getConstants();
		int SETTINGS_ON = constants != null ? constants.MINIMAP_SETTINGS_ON : -1;
		boolean flag = client.getMinimapSettings() == SETTINGS_ON;
		int sin = SIN_TABLE[mapAngle];
		int cos = COS_TABLE[mapAngle];
		if (!flag) {
			int scale = 256 + client.getMinimapScale();
			sin = 256 * sin / scale;
			cos = 256 * cos / scale;
		}
		int _x = cos * pX + sin * pY >> 14;
		int _y = cos * pY - sin * pX >> 14;
		_x += component.getScrollWidth() / 2;
		_y *= -1;
		_y += component.getScrollHeight() / 2;
		if (_x <= 4 || _x >= component.getScrollWidth() - 4 ||
				_y <= 4 || _y >= component.getScrollHeight() - 4) {
			return new Point(-1, -1);
		}

		Point basePoint = component.getAbsoluteLocation();
		int screen_x = _x + (int) basePoint.getX();
		int screen_y = _y + (int) basePoint.getY();
		Point p = new Point(screen_x, screen_y);
		Rectangle t = new Rectangle(p.x - 6, p.y - 6, 12, 12);//entire tile and a half sized 'buffer' area
		for (int i = 17; i <= 21; i++) {
			if (ctx.widgets.get(1465, i).getViewportRect().intersects(t)) {
				return new Point(-1, -1);
			}
		}
		return p;
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
