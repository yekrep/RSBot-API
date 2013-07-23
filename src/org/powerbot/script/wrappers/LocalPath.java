package org.powerbot.script.wrappers;

import java.util.EnumSet;

import org.powerbot.script.internal.methods.Map;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.methods.MethodContext;

public class LocalPath extends Path {
	private Locatable destination;
	private Tile tile;
	private TilePath tilePath;

	private Map map;

	public LocalPath(MethodContext factory, Map map, Locatable destination) {
		super(factory);
		this.destination = destination;
		this.map = map;
	}

	@Override
	public boolean traverse(EnumSet<TraversalOption> options) {
		return isValid() && tilePath.traverse(options);
	}

	@Override
	public boolean isValid() {
		Tile end = destination.getLocation();
		if (end == null || end == Tile.NIL) {
			return false;
		}
		if (end.equals(tile)) {
			return true;
		}
		tile = end;
		Tile start = ctx.players.local().getLocation();
		Tile base = ctx.game.getMapBase();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) {
			return false;
		}
		start = start.derive(-base.x, -base.y);
		end = end.derive(-base.x, -base.y);
		Map.Node[] path = map.getPath(start.getX(), start.getY(), end.getX(), end.getY(), ctx.game.getPlane());
		if (path.length > 0) {
			Tile[] arr = new Tile[path.length];
			for (int i = 0; i < path.length; i++) {
				arr[i] = base.derive(path[i].x, path[i].y);
			}
			tilePath = ctx.movement.newTilePath(arr);
			return true;
		}
		return false;
	}

	@Override
	public Tile getNext() {
		return isValid() ? tilePath.getNext() : null;
	}

	@Override
	public Tile getStart() {
		return null;
	}

	@Override
	public Tile getEnd() {
		return destination.getLocation();
	}
}
