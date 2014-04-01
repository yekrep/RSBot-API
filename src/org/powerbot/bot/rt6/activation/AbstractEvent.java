package org.powerbot.bot.rt6.activation;

import java.util.EventListener;
import java.util.EventObject;

public abstract class AbstractEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private static final Object SOURCE = new Object();
	public int id = -1;

	protected AbstractEvent() {
		super(SOURCE);
	}

	public abstract void dispatch(final EventListener eventListener);

	protected void setId(final int id) {
		this.id = id;
	}
}
