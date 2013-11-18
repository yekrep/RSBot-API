package org.powerbot.bot;

import java.awt.AWTEvent;

public class RawAWTEvent extends AWTEvent {
	private static final long serialVersionUID = -1409783285345666039L;
	private AWTEvent event;

	public RawAWTEvent(AWTEvent event) {
		super(event.getSource(), event.getID());
		this.event = event;
	}

	public AWTEvent getEvent() {
		return event;
	}
}
