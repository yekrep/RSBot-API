package org.powerbot.script.rs3;

import java.util.EnumSet;

import org.powerbot.script.Tile;

public abstract class Path extends ClientAccessor {
	public Path(final ClientContext factory) {
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
