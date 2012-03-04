package org.powerbot.game.bot;

import org.powerbot.game.client.Callback;
import org.powerbot.game.client.Render;
import org.powerbot.game.event.MessageEvent;

public class CallbackImpl implements Callback {
	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	public void updateRenderInfo(final Render render) {
	}

	public void notifyMessage(int id, String sender, String message) {
		bot.eventDispatcher.dispatch(new MessageEvent(id, sender, message));
		System.out.println("[" + id + "] " + sender + ": " + message);
	}
}
