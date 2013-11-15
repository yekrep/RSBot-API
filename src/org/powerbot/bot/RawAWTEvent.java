package org.powerbot.bot;

import java.awt.AWTEvent;

public class RawAWTEvent extends AWTEvent {
	private AWTEvent event;

	public RawAWTEvent(AWTEvent event) {
		super(event.getSource(), event.getID());
		this.event = event;
	}

	public AWTEvent getEvent() {
		return event;
	}
}
