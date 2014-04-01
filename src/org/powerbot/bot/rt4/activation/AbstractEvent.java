package org.powerbot.bot.rt4.activation;

import java.util.EventListener;
import java.util.EventObject;

public abstract class AbstractEvent extends EventObject {
	protected final int id;

	public AbstractEvent(final int id) {
		super(new Object());
		this.id = id;
	}

	public abstract void call(final EventListener e);
}
