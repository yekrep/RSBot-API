package org.powerbot.event;

import java.util.EventListener;
import java.util.EventObject;

public abstract class GameEvent extends EventObject {
	public final int type;

	public GameEvent(final Object source, final int type) {
		super(source);
		this.type = type;
	}

	public abstract void dispatch(final EventListener eventListener);
}
