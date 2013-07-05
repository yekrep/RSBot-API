package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.internal.wrappers.CollisionFlag;
import org.powerbot.script.internal.wrappers.CollisionMap;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.lang.Targetable;
import org.powerbot.script.util.Delay;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TileMatrix;
import org.powerbot.script.wrappers.TilePath;

import java.awt.Point;

public class Movement extends MethodProvider {
	private static final int WIDGET = 750;
	private static final int COMPONENT_RUN = 2;
	private static final int COMPONENT_RUN_ENERGY = 6;
	private static final int SETTING_RUN_ENABLED = 463;

	public Movement(MethodContext factory) {
		super(factory);
	}

	public TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) {
			throw new IllegalArgumentException("tiles are null");
		}
		return new TilePath(ctx, tiles);
	}

	public Tile getDestination() {
		Client client = ctx.getClient();
		if (client == null) {
			return null;
		}
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) {
			return null;
		}
		return ctx.game.getMapBase().derive(dX, dY);
	}

	public boolean stepTowards(final Locatable locatable) {
		final Tile loc = locatable.getLocation();
		return ctx.mouse.click(new Targetable() {
			private TileMatrix tile = loc.getMatrix(ctx);

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

	public boolean setRunning(final boolean run) {
		if (isRunning() != run) {
			final Component c = ctx.widgets.get(WIDGET, COMPONENT_RUN);
			if (c != null && c.click(true)) {
				for (int i = 0; i < 20 && isRunning() != run; i++) {
					Delay.sleep(100, 200);
				}
			}
		}
		return isRunning() == run;
	}

	public boolean isRunning() {
		return ctx.settings.get(SETTING_RUN_ENABLED) == 0x1;
	}

	public int getEnergyLevel() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_RUN_ENERGY);
		if (c != null && c.isValid()) {
			try {
				final String text = c.getText();
				if (text != null) {
					return Integer.parseInt(text.trim());
				}
			} catch (final NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public Tile getClosestOnMap(Tile tile) {
		if (tile.getMatrix(ctx).isOnMap()) {
			return tile;
		}

		final Tile location = ctx.players.getLocal().getLocation();
		tile = tile.derive(-location.getX(), -location.getY());
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
	}

	public int pathDistance(Tile start, Tile end, final boolean findAdjacent) {
		CollisionMap[] maps = ctx.map.getPlanes();
		int plane = ctx.game.getPlane();
		if (plane < 0 || plane >= maps.length) return -1;
		CollisionMap map = maps[plane];
		if (map == null) return -1;

		Tile base = ctx.game.getMapBase();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) return -1;
		start = start.derive(-base.x, -base.y);
		end = end.derive(-base.x, -base.y);

		int startX = start.getX(), startY = start.getY();
		int endX = end.getX(), endY = end.getY();
		int xSize = map.getSizeX() - 6, ySize = map.getSizeY() - 6;
		CollisionFlag[][] values = new CollisionFlag[xSize][ySize];
		int[][] blocks = new int[xSize][ySize];
		final int[][] prev = new int[xSize][ySize];
		final int[][] dist = new int[xSize][ySize];
		for (int xx = 0; xx < xSize; xx++) {
			for (int yy = 0; yy < ySize; yy++) {
				values[xx][yy] = map.getClippingValueAtLocal(xx, yy);
				blocks[xx][yy] = values[xx][yy].getType();
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
			if (curr_y > 0 &&
					prev[curr_x][curr_y - 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.SOUTH) &&
					!values[curr_x][curr_y - 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				path_x[path_ptr] = curr_x;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x][curr_y - 1] = 1;
				dist[curr_x][curr_y - 1] = cost;
			}
			if (curr_x > 0 &&
					prev[curr_x - 1][curr_y] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.WEST) &&
					!values[curr_x - 1][curr_y].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y] = 2;
				dist[curr_x - 1][curr_y] = cost;
			}
			if (curr_y < 104 - 1 && prev[curr_x][curr_y + 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.NORTH) &&
					!values[curr_x][curr_y + 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				path_x[path_ptr] = curr_x;
				path_y[path_ptr] = curr_y + 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x][curr_y + 1] = 4;
				dist[curr_x][curr_y + 1] = cost;
			}
			if (curr_x < 104 - 1 && prev[curr_x + 1][curr_y] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.EAST) &&
					!values[curr_x + 1][curr_y].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				path_x[path_ptr] = curr_x + 1;
				path_y[path_ptr] = curr_y;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x + 1][curr_y] = 8;
				dist[curr_x + 1][curr_y] = cost;
			}
			if (curr_x > 0 && curr_y > 0 && prev[curr_x - 1][curr_y - 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.SOUTHWEST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.WEST))) &&
					!values[curr_x - 1][curr_y - 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!values[curr_x][curr_y - 1].marked(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!values[curr_x - 1][curr_y].marked(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y - 1] = 3;
				dist[curr_x - 1][curr_y - 1] = cost;
			}
			if (curr_x > 0 && curr_y < ySize - 1 && prev[curr_x - 1][curr_y + 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.NORTHWEST.mark(CollisionFlag.NORTH.mark(CollisionFlag.WEST))) &&
					!values[curr_x - 1][curr_y + 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!values[curr_x][curr_y + 1].marked(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!values[curr_x - 1][curr_y].marked(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				path_x[path_ptr] = curr_x - 1;
				path_y[path_ptr] = curr_y + 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x - 1][curr_y + 1] = 6;
				dist[curr_x - 1][curr_y + 1] = cost;
			}
			if (curr_x < ySize - 1 && curr_y > 0 && prev[curr_x - 1][curr_y - 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.SOUTHEAST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.EAST))) &&
					!values[curr_x + 1][curr_y - 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!values[curr_x][curr_y - 1].marked(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!values[curr_x + 1][curr_y].marked(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				path_x[path_ptr] = curr_x + 1;
				path_y[path_ptr] = curr_y - 1;
				path_ptr = (path_ptr + 1) % pathLength;
				prev[curr_x + 1][curr_y - 1] = 9;
				dist[curr_x + 1][curr_y - 1] = cost;
			}
			if (curr_x < xSize - 1 && curr_y < ySize - 1 && prev[curr_x + 1][curr_y + 1] == 0 &&
					!values[curr_x][curr_y].marked(CollisionFlag.NORTHEAST.mark(CollisionFlag.NORTH.mark(CollisionFlag.WEST))) &&
					!values[curr_x + 1][curr_y + 1].marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!values[curr_x][curr_y + 1].marked(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!values[curr_x + 1][curr_y].marked(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
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
