package org.powerbot.os.bot;

import java.awt.AWTEvent;

public class RawAWTEvent extends AWTEvent {
	private static final long serialVersionUID = -1409783285345666039L;
	private final AWTEvent event;

	public RawAWTEvent(final AWTEvent event) {
		super(event.getSource(), event.getID());
		this.event = event;
	}

	public AWTEvent getEvent() {
		return event;
	}
}
