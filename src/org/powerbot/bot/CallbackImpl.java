package org.powerbot.bot;

import org.powerbot.client.Callback;
import org.powerbot.client.Render;
import org.powerbot.event.MessageEvent;
import org.powerbot.script.methods.Game;

/**
 * An implementation of callback responsible for processing client callbacks to appropriate bot functions.
 *
 * @author Timer
 */
public class CallbackImpl implements Callback {
	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	/**
	 * Updates this bot's render information used to calculate screen vectors.
	 *
	 * @param render The render provided from the game.
	 */
	public void updateRenderInfo(final Render render) {
		Game.updateToolkit(render);
	}

	/**
	 * Notifies the bot of of a message dispatched in the game.
	 *
	 * @param id      The Id of the message dispatched.
	 * @param sender  The name of the sender of this message.
	 * @param message The message contents the sender sent.
	 */
	public void notifyMessage(final int id, final String sender, final String message) {
		bot.getEventMulticaster().dispatch(new MessageEvent(id, sender, message));
	}
}
