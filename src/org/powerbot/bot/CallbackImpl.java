package org.powerbot.bot;

import org.powerbot.client.Callback;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.Render;
import org.powerbot.event.MessageEvent;

public class CallbackImpl implements Callback {
	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public void updateRenderInfo(final Render render) {
		bot.getMethodContext().game.updateToolkit(render);
	}

	@Override
	public void notifyMessage(final int id, final String sender, final String message) {
		bot.getEventMulticaster().dispatch(new MessageEvent(id, sender, message));
	}

	@Override
	public void notifyObjectDefinitionLoad(RSObjectDef def) {
		//TODO
	}
}
