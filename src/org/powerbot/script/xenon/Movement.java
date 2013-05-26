package org.powerbot.script.xenon;

import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.client.RSGroundData;
import org.powerbot.client.RSInfo;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Locatable;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Targetable;
import org.powerbot.script.xenon.wrappers.Tile;
import org.powerbot.script.xenon.wrappers.TilePath;

import static org.powerbot.script.xenon.wrappers.Path.OBJECT_BLOCK;
import static org.powerbot.script.xenon.wrappers.Path.OBJECT_TILE;
import static org.powerbot.script.xenon.wrappers.Path.WALL_EAST;
import static org.powerbot.script.xenon.wrappers.Path.WALL_NORTH;
import static org.powerbot.script.xenon.wrappers.Path.WALL_NORTHEAST;
import static org.powerbot.script.xenon.wrappers.Path.WALL_NORTHWEST;
import static org.powerbot.script.xenon.wrappers.Path.WALL_SOUTH;
import static org.powerbot.script.xenon.wrappers.Path.WALL_SOUTHEAST;
import static org.powerbot.script.xenon.wrappers.Path.WALL_SOUTHWEST;
import static org.powerbot.script.xenon.wrappers.Path.WALL_WEST;

public class Movement {
	private static final int WIDGET = 750;
	private static final int COMPONENT_RUN = 2;
	private static final int COMPONENT_RUN_ENERGY = 6;
	private static final int SETTING_RUN_ENABLED = 463;

	public static TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) throw new IllegalArgumentException("tiles are null");
		return new TilePath(tiles);
	}

	public static Tile getDestination() {
		final Client client = Bot.client();
		if (client == null) return null;
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) return null;
		final Tile base = Game.getMapBase();
		return base != null ? base.derive(dX, dY) : null;
	}

	public static Point getCollisionOffset() {
		final Client client = Bot.client();
		if (client == null) return null;
		final int plane = client.getPlane();
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? new Point(ground.getX(), ground.getY()) : null;
	}

	public static int[][] getCollisionMeta() {
		final Client client = Bot.client();
		if (client == null) return null;
		final int plane = client.getPlane();
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? ground.getBlocks() : null;
	}

	public static int getDistance(Tile start, Tile end, final boolean findAdjacent) {
		final Tile base = Game.getMapBase();
		if (base == null || start == null || end == null) return -1;
		start = start.derive(-base.x, -base.y);
		end = end.derive(-base.x, -base.y);
		final Point pos = getCollisionOffset();
		final int[][] meta = getCollisionMeta();
		if (pos == null || meta == null) return -1;
		final int startX = start.getX() - pos.x, startY = start.getY() - pos.y;
		final int endX = end.getX() - pos.x, endY = end.getY() - pos.y;
		final int[][] prev = new int[104][104];
		final int[][] dist = new int[104][104];
		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				prev[xx][yy] = 0;
				dist[xx][yy] = Integer.MAX_VALUE;
			}
		}
		final int[] path_x = new int[4000];
		final int[] path_y = new int[4000];
		int curr_x = startX;
		int curr_y = startY;
		prev[startX][startY] = 99;
		dist[startX][startY] = 0;
		int path_ptr = 0;
		int step_ptr = 0;
		path_x[path_ptr] = startX;
		path_y[path_ptr++] = startY;
		final int pathLength = path_x.length;
		boolean foundPath = false;
		while (step_ptr != path_ptr) {
			curr_x = path_x[step_ptr];
			curr_y = path_y[step_ptr];
			if (Math.abs(curr_x - endX) + Math.abs(curr_y - endY) == (findAdjacent ? 1 : 0)) {
				foundPath = true;
				break;
			}
			step_ptr = (step_ptr + 1) % pathLength;
			final int cost = dist[curr_x][curr_y] + 1;
			final int here = meta[curr_x][curr_y];
			if (curr_y > 0 && (here & WALL_SOUTH) == 0 && (meta[curr_x][curr_y - 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0) {
				path_x[path_ptr] = curr_x;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x][curr_y - 1] = 1;
				dist[curr_x][curr_y - 1] = cost;
			}
			if (curr_x > 0 && (here & WALL_WEST) == 0 && (meta[curr_x - 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK)) == 0) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y] = 2;
				dist[curr_x - 1][curr_y] = cost;
			}
			if (curr_y < 104 - 1 && (here & WALL_NORTH) == 0 && (meta[curr_x][curr_y + 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0) {
				path_x[path_ptr] = curr_x;
				path_y[path_ptr] = curr_y + 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x][curr_y + 1] = 4;
				dist[curr_x][curr_y + 1] = cost;
			}
			if (curr_x < 104 - 1 && (here & WALL_EAST) == 0 && (meta[curr_x + 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK)) == 0) {
				path_x[path_ptr] = curr_x + 1;
				path_y[path_ptr] = curr_y;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x + 1][curr_y] = 8;
				dist[curr_x + 1][curr_y] = cost;
			}
			if (curr_x > 0 && curr_y > 0 && (here & (WALL_SOUTHWEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (meta[curr_x - 1][curr_y - 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0
					&& (meta[curr_x][curr_y - 1] & (OBJECT_TILE | OBJECT_BLOCK | WALL_WEST)) == 0
					&& (meta[curr_x - 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK | WALL_SOUTH)) == 0) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y - 1] = 3;
				dist[curr_x - 1][curr_y - 1] = cost;
			}
			if (curr_x > 0 && curr_y < 104 - 1 && (here & (WALL_NORTHWEST | WALL_NORTH | WALL_WEST)) == 0
					&& (meta[curr_x - 1][curr_y + 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0
					&& (meta[curr_x][curr_y + 1] & (OBJECT_TILE | OBJECT_BLOCK | WALL_WEST)) == 0
					&& (meta[curr_x - 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK | WALL_NORTH)) == 0) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y + 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y + 1] = 6;
				dist[curr_x - 1][curr_y + 1] = cost;
			}
			if (curr_x < 104 - 1 && curr_y > 0 && (here & (WALL_SOUTHEAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (meta[curr_x + 1][curr_y - 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0
					&& (meta[curr_x][curr_y - 1] & (OBJECT_TILE | OBJECT_BLOCK | WALL_EAST)) == 0
					&& (meta[curr_x + 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK | WALL_SOUTH)) == 0) {
				path_x[path_ptr] = curr_x + 1;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x + 1][curr_y - 1] = 9;
				dist[curr_x + 1][curr_y - 1] = cost;
			}
			if (curr_x < 104 - 1 && curr_y < 104 - 1 && (here & (WALL_NORTHEAST | WALL_NORTH | WALL_EAST)) == 0
					&& (meta[curr_x + 1][curr_y + 1] & (OBJECT_TILE | OBJECT_BLOCK)) == 0
					&& (meta[curr_x][curr_y + 1] & (OBJECT_TILE | OBJECT_BLOCK | WALL_EAST)) == 0
					&& (meta[curr_x + 1][curr_y] & (OBJECT_TILE | OBJECT_BLOCK | WALL_NORTH)) == 0) {
				path_x[path_ptr] = curr_x + 1;
				path_y[path_ptr] = curr_y + 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x + 1][curr_y + 1] = 12;
				dist[curr_x + 1][curr_y + 1] = cost;
			}
		}
		return foundPath ? dist[curr_x][curr_y] : -1;
	}

	public static boolean stepTowards(final Locatable locatable) {
		final Tile tile = locatable.getLocation();
		return Mouse.click(new Targetable() {
			@Override
			public Point getInteractPoint() {
				return tile.getMapPoint();
			}

			@Override
			public Point getNextPoint() {
				return tile.getMapPoint();
			}

			@Override
			public Point getCenterPoint() {
				return tile.getMapPoint();
			}

			@Override
			public boolean contains(final Point point) {
				return point.distance(tile.getMapPoint()) < Math.sqrt(3);
			}
		}, true);
	}

	public static boolean setRunning(final boolean run) {
		if (isRunning() != run) {
			final Component c = Widgets.get(WIDGET, COMPONENT_RUN);
			if (c != null && c.click(true)) for (int i = 0; i < 20 && isRunning() != run; i++) Delay.sleep(100, 200);
		}
		return isRunning() == run;
	}

	public static boolean isRunning() {
		return Settings.get(SETTING_RUN_ENABLED) == 0x1;
	}

	public static int getEnergyLevel() {
		final Component c = Widgets.get(WIDGET, COMPONENT_RUN_ENERGY);
		if (c != null && c.isValid()) try {
			final String text = c.getText();
			if (text != null) return Integer.parseInt(text.trim());
		} catch (final NumberFormatException ignored) {
		}
		return -1;
	}

	public static Tile getClosestOnMap(Tile tile) {
		if (tile.isOnMap()) {
			return tile;
		}

		final Tile location = Players.getLocal().getLocation();
		tile = tile.derive(-location.getX(), -location.getY());
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
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
}
