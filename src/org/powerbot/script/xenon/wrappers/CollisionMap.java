package org.powerbot.script.xenon.wrappers;

import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSGroundData;
import org.powerbot.game.client.RSInfo;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Players;

public class CollisionMap {
	public static final int WALL_NORTHWEST = 0x1;
	public static final int WALL_NORTH = 0x2;
	public static final int WALL_NORTHEAST = 0x4;
	public static final int WALL_EAST = 0x8;
	public static final int WALL_SOUTHEAST = 0x10;
	public static final int WALL_SOUTH = 0x20;
	public static final int WALL_SOUTHWEST = 0x40;
	public static final int WALL_WEST = 0x80;
	public static final int OBJECT_TILE = 0x100;
	public static final int WALL_BLOCK_NORTHWEST = 0x200;
	public static final int WALL_BLOCK_NORTH = 0x400;
	public static final int WALL_BLOCK_NORTHEAST = 0x800;
	public static final int WALL_BLOCK_EAST = 0x1000;
	public static final int WALL_BLOCK_SOUTHEAST = 0x2000;
	public static final int WALL_BLOCK_SOUTH = 0x4000;
	public static final int WALL_BLOCK_SOUTHWEST = 0x8000;
	public static final int WALL_BLOCK_WEST = 0x10000;
	public static final int OBJECT_BLOCK = 0x20000;
	public static final int DECORATION_BLOCK = 0x40000;
	public static final int WALL_ALLOW_RANGE_NORTHWEST = 0x400000;
	public static final int WALL_ALLOW_RANGE_NORTH = 0x800000;
	public static final int WALL_ALLOW_RANGE_NORTHEAST = 0x1000000;
	public static final int WALL_ALLOW_RANGE_EAST = 0x2000000;
	public static final int WALL_ALLOW_RANGE_SOUTHEAST = 0x4000000;
	public static final int WALL_ALLOW_RANGE_SOUTH = 0x8000000;
	public static final int WALL_ALLOW_RANGE_SOUTHWEST = 0x10000000;
	public static final int WALL_ALLOW_RANGE_WEST = 0x20000000;
	public static final int OBJECT_ALLOW_RANGE = 0x40000000;
	private final int plane;

	public CollisionMap(final int plane) {
		this.plane = plane;
	}

	public Point getPosition() {
		final Client client = Bot.client();
		if (client == null) return null;
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? new Point(ground.getX(), ground.getY()) : null;
	}

	public int[][] getMeta() {
		final Client client = Bot.client();
		if (client == null) return null;
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? ground.getBlocks() : null;
	}

	public boolean canReach(Tile tile) {
		final Tile base = Game.getMapBase();
		final Player player = Players.getLocal();
		Tile loc = player != null ? player.getLocation() : null;
		if (base == null || loc == null) return false;
		tile = tile.derive(-base.x, -base.y);
		loc = loc.derive(-base.x, -base.y);
		return tile.getPlane() == base.getPlane() && getDistance(loc.x, loc.y, tile.x, tile.y, false) != -1;
	}

	public int getDistance(final int startX, final int startY, final int endX, final int endY, final boolean findAdjacent) {
		final Point pos = getPosition();
		final int[][] meta = getMeta();
		if (pos == null || meta == null) return -1;
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
}
