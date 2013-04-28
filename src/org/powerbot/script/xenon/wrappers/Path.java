package org.powerbot.script.xenon.wrappers;

import java.util.EnumSet;

public abstract class Path {
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
