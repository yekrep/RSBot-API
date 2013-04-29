package org.powerbot.script.xenon.wrappers;

import java.awt.Point;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.powerbot.script.xenon.Calculations;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Movement;
import org.powerbot.script.xenon.Players;

public class LocalPath extends Path {
	private static final int WALL_NORTHWEST = 0x1;
	private static final int WALL_NORTH = 0x2;
	private static final int WALL_NORTHEAST = 0x4;
	private static final int WALL_EAST = 0x8;
	private static final int WALL_SOUTHEAST = 0x10;
	private static final int WALL_SOUTH = 0x20;
	private static final int WALL_SOUTHWEST = 0x40;
	private static final int WALL_WEST = 0x80;
	private static final int BLOCKED = 0x200100;
	private final Tile end;
	private Tile base;
	private TilePath tilePath;

	public LocalPath(final Tile end) {
		this.end = end;
		this.base = null;
		this.tilePath = null;
	}

	@Override
	public boolean traverse(final EnumSet<TraversalOption> options) {
		return getNext() != null && tilePath.traverse(options);
	}

	@Override
	public boolean isValid() {
		return getNext() != null && Calculations.distanceTo(end) > Math.sqrt(3);
	}

	@Override
	public Tile getNext() {
		final Tile base = Game.getMapBase();
		if (base == null) return null;
		final Player player = Players.getLocal();
		final Tile loc = player != null ? player.getLocation() : null;
		if (loc != null && !base.equals(this.base)) {
			this.base = base;
			final Tile[] tiles = findTilePath(loc, end);
			if (tiles == null) {
				this.base = null;
				return null;
			}
			tilePath = Movement.newTilePath(tiles);
			return tilePath.getNext();
		}
		return null;
	}

	@Override
	public Tile getStart() {
		return null;
	}

	@Override
	public Tile getEnd() {
		return end;
	}

	private Tile[] findTilePath(final Tile start, final Tile end) {
		if (Game.getPlane() != end.getPlane()) return null;
		final int base_x = base.getX(), base_y = base.getY();
		final int curr_x = start.getX() - base_x, curr_y = start.getY() - base_y;
		int dest_x = end.getX() - base_x, dest_y = end.getY() - base_y;
		if (dest_x < 0 || dest_y < 0 || dest_x > 103 || dest_y > 103) return null;
		final CollisionMap map = Movement.getCollisionMap();
		if (map == null) return null;
		final int[][] meta = map.getMeta();
		final Point offset = map.getPosition();
		if (meta == null || offset == null) return null;
		final int offX = offset.x, offY = offset.y;

		final HashSet<Vertex> open = new HashSet<>();
		final HashSet<Vertex> closed = new HashSet<>();
		Vertex curr = new Vertex(curr_x, curr_y);
		final Vertex dest = new Vertex(dest_x, dest_y);

		curr.f = heuristic(curr, dest);
		open.add(curr);

		while (!open.isEmpty()) {
			curr = lowest_f(open);
			if (curr.equals(dest)) {
				return path(curr, base_x, base_y, end.getPlane());
			}
			open.remove(curr);
			closed.add(curr);
			for (final Vertex next : successors(curr, meta, offX, offY)) {
				if (!closed.contains(next)) {
					final double t = curr.g + dist(curr, next);
					boolean use_t = false;
					if (!open.contains(next)) {
						open.add(next);
						use_t = true;
					} else if (t < next.g) {
						use_t = true;
					}
					if (use_t) {
						next.prev = curr;
						next.g = t;
						next.f = t + heuristic(next, dest);
					}
				}
			}
		}
		return null;
	}

	private double heuristic(final Vertex start, final Vertex end) {
		final double dx = Math.abs(start.x - end.x);
		final double dy = Math.abs(start.y - end.y);
		final double diag = Math.min(dx, dy);
		final double straight = dx + dy;
		return Math.sqrt(2.0) * diag + straight - 2 * diag;
	}

	private double dist(final Vertex start, final Vertex end) {
		if (start.x != end.x && start.y != end.y) {
			return 1.41421356;
		} else {
			return 1.0;
		}
	}

	private Vertex lowest_f(final Set<Vertex> open) {
		Vertex best = null;
		for (final Vertex t : open) {
			if (best == null || t.f < best.f) {
				best = t;
			}
		}
		return best;
	}

	private Tile[] path(final Vertex end, final int base_x, final int base_y, final int plane) {
		final LinkedList<Tile> path = new LinkedList<>();
		Vertex p = end;
		while (p != null) {
			path.addFirst(p.get(base_x, base_y, plane));
			p = p.prev;
		}
		return path.toArray(new Tile[path.size()]);
	}

	private List<Vertex> successors(final Vertex t, final int[][] meta, final int offX, final int offY) {
		final LinkedList<Vertex> tiles = new LinkedList<>();
		final int x = t.x, y = t.y;
		final int f_x = x - offX, f_y = y - offY;
		final int here = meta[f_x][f_y];
		if (f_y > 0 && (here & WALL_SOUTH) == 0 && (meta[f_x][f_y - 1] & BLOCKED) == 0) {
			tiles.add(new Vertex(x, y - 1));
		}
		if (f_x > 0 && (here & WALL_WEST) == 0 && (meta[f_x - 1][f_y] & BLOCKED) == 0) {
			tiles.add(new Vertex(x - 1, y));
		}
		if (f_y < 104 - 1 && (here & WALL_NORTH) == 0 && (meta[f_x][f_y + 1] & BLOCKED) == 0) {
			tiles.add(new Vertex(x, y + 1));
		}
		if (f_x < 104 - 1 && (here & WALL_EAST) == 0 && (meta[f_x + 1][f_y] & BLOCKED) == 0) {
			tiles.add(new Vertex(x + 1, y));
		}
		if (f_x > 0 && f_y > 0 && (here & (WALL_SOUTHWEST | WALL_SOUTH | WALL_WEST)) == 0
				&& (meta[f_x - 1][f_y - 1] & BLOCKED) == 0
				&& (meta[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
				&& (meta[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
			tiles.add(new Vertex(x - 1, y - 1));
		}
		if (f_x > 0 && f_y < 104 - 1 && (here & (WALL_NORTHWEST | WALL_NORTH | WALL_WEST)) == 0
				&& (meta[f_x - 1][f_y + 1] & BLOCKED) == 0
				&& (meta[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
				&& (meta[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
			tiles.add(new Vertex(x - 1, y + 1));
		}
		if (f_x < 104 - 1 && f_y > 0 && (here & (WALL_SOUTHEAST | WALL_SOUTH | WALL_EAST)) == 0
				&& (meta[f_x + 1][f_y - 1] & BLOCKED) == 0
				&& (meta[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
				&& (meta[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
			tiles.add(new Vertex(x + 1, y - 1));
		}
		if (f_x < 104 - 1 && f_y < 104 - 1 && (here & (WALL_NORTHEAST | WALL_NORTH | WALL_EAST)) == 0
				&& (meta[f_x + 1][f_y + 1] & BLOCKED) == 0
				&& (meta[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
				&& (meta[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
			tiles.add(new Vertex(x + 1, y + 1));
		}
		return tiles;
	}

	public static final class Vertex {
		public final int x, y;
		public Vertex prev;
		public double g, f;

		public Vertex(final int x, final int y) {
			this.x = x;
			this.y = y;
			g = f = 0;
		}

		@Override
		public int hashCode() {
			return x << 4 | y;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Vertex) {
				final Vertex n = (Vertex) o;
				return x == n.x && y == n.y;
			}
			return false;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		public Tile get(final int baseX, final int baseY, final int plane) {
			return new Tile(x + baseX, y + baseY, plane);
		}
	}
}
