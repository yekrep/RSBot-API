package org.powerbot.event;

import java.util.EventListener;
import java.util.EventObject;

public abstract class GameEvent extends EventObject {
	private static final Object SOURCE = new Object();
	public int type = -1;

	public GameEvent() {
		super(SOURCE);
	}

	public abstract void dispatch(final EventListener eventListener);

	protected void setType(final int type) {
		this.type = type;
	}
}
