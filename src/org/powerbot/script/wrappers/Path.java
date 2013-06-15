package org.powerbot.script.wrappers;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

import java.util.EnumSet;

public abstract class Path extends MethodProvider {
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

	public Path(MethodContext factory) {
		super(factory);
	}

	public abstract boolean traverse(final EnumSet<TraversalOption> options);

	public boolean traverse() {
		return traverse(EnumSet.of(TraversalOption.HANDLE_RUN, TraversalOption.SPACE_ACTIONS));
	}

	public abstract boolean isValid();

	public abstract Tile getNext();

	public abstract Tile getStart();

	public abstract Tile getEnd();

	public static enum TraversalOption {
		HANDLE_RUN, SPACE_ACTIONS
	}
}
