package org.powerbot.script.rt6;

import java.util.EnumSet;

import org.powerbot.script.Tile;

/**
 * Path
 */
public abstract class Path extends ClientAccessor {
	public Path(final ClientContext factory) {
		super(factory);
	}

	public abstract boolean traverse(final EnumSet<TraversalOption> options);

	public boolean traverse() {
		return traverse(EnumSet.of(TraversalOption.HANDLE_RUN, TraversalOption.SPACE_ACTIONS));
	}

	public abstract boolean valid();

	public abstract Tile next();

	public abstract Tile start();

	public abstract Tile end();

	public enum TraversalOption {
		HANDLE_RUN, SPACE_ACTIONS
	}
}
