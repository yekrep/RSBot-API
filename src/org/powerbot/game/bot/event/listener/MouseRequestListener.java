package org.powerbot.game.bot.event.listener;

import org.powerbot.game.bot.handler.input.util.MouseNode;

public interface MouseRequestListener {
	public boolean processMouseRequest(MouseNode mouseNode);
}
