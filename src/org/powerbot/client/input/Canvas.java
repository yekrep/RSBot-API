package org.powerbot.client.input;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private final Bot bot;

	public Canvas() {
		super();
		this.bot = BotChrome.getInstance().getBot();
	}

	@Override
	public Graphics getGraphics() {
		if (bot.getMethodContext().game.toolkit.graphicsIndex != 0) {
			return super.getGraphics();
		}

		final Graphics game = bot.getGameBuffer();
		super.getGraphics().drawImage(bot.getBufferImage(), 0, 0, null);
		return game;
	}

	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		final BufferedImage image = bot.getBufferImage();
		if (image.getWidth() != w || image.getHeight() != h) {
			bot.resize(w, h);
		}
	}
}