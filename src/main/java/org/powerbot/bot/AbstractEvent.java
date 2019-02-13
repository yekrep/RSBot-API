package org.powerbot.bot;

import java.util.EventListener;
import java.util.EventObject;

public abstract class AbstractEvent extends EventObject {
	public final int eventId;

	public AbstractEvent(final int eventId) {
		super(new Object());
		this.eventId = eventId;
	}

	public abstract void call(final EventListener e);
}
