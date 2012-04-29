package org.powerbot.game.bot.event;

import java.awt.Point;
import java.util.EventListener;

import org.powerbot.event.EventDispatcher;
import org.powerbot.event.GeneralEvent;
import org.powerbot.game.bot.event.listener.HumanInputListener;

/**
 * A message event that is dispatched when a new message is dispatched in the game.
 *
 * @author Timer
 */
public class HumanInputEvent extends GeneralEvent {
	private static final long serialVersionUID = 1L;
	private Point point;
	private boolean accepted;

	public HumanInputEvent() {
		setType(EventDispatcher.MOUSE_REQUEST_EVENT);
		accepted = true;
	}

	public void init(final Point point) {
		this.accepted = true;
		this.point = point;
	}

	public boolean accepted() {
		return accepted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(final EventListener eventListener) {
		accepted = accepted && ((HumanInputListener) eventListener).processRequest(point);
	}
}
