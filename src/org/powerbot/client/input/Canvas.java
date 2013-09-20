package org.powerbot.client.input;

import java.awt.Graphics;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private final Bot bot;

	public Canvas() {
		super();
		this.bot = BotChrome.getInstance().getBot();
	}

	@Override
	public void setVisible(boolean visible) {
		BotChrome.getInstance().target(this);
		super.setVisible(visible);
	}

	@Override
	public Graphics getGraphics() {
		final Graphics game = bot.getGameBuffer();
		super.getGraphics().drawImage(bot.getBufferImage(), 0, 0, null);
		return game;
	}
}