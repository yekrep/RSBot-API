package org.powerbot.game.bot;

import org.powerbot.game.client.Callback;
import org.powerbot.game.client.Render;
import org.powerbot.game.event.MessageEvent;

public class CallbackImpl implements Callback {
	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public void updateRenderInfo(final Render render) {
	}

	@Override
	public void notifyMessage(final int id, final String sender, final String message) {
		bot.eventDispatcher.dispatch(new MessageEvent(id, sender, message));
		System.out.println("[" + id + "] " + sender + ": " + message);
	}
}
