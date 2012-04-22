package org.powerbot.game.bot.event;

import java.util.EventListener;

import org.powerbot.event.EventDispatcher;
import org.powerbot.event.GeneralEvent;
import org.powerbot.game.bot.event.listener.MouseRequestListener;
import org.powerbot.game.bot.handler.input.util.MouseNode;

/**
 * A message event that is dispatched when a new message is dispatched in the game.
 *
 * @author Timer
 */
public class MouseRequestEvent extends GeneralEvent {
	private static final long serialVersionUID = 1L;
	private MouseNode mouseNode;
	private boolean accepted;

	public MouseRequestEvent() {
		setType(EventDispatcher.MOUSE_REQUEST_EVENT);
		accepted = true;
	}

	public void init(final MouseNode mouseNode) {
		this.accepted = true;
		this.mouseNode = mouseNode;
	}

	public boolean accepted() {
		return accepted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(final EventListener eventListener) {
		accepted = accepted && ((MouseRequestListener) eventListener).processMouseRequest(mouseNode);
	}
}
