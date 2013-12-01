package org.powerbot.client.input;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.bot.Bot;
import org.powerbot.bot.EventCallback;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -8630774417757577975L;
	private final Bot bot;

	public Canvas() {
		super();
		final BotChrome chrome = BotChrome.getInstance();
		final SelectiveEventQueue queue = SelectiveEventQueue.getInstance();
		this.bot = chrome.getBot();

		SelectiveEventQueue.pushSelectiveQueue();
		queue.block(this, new EventCallback() {
			@Override
			public void execute(final AWTEvent event) {
			}
		});

		if (SelectiveEventQueue.getInstance().isBlocking()) {
			queue.focus();
		}
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
	public void setSize(final int w, final int h) {
		super.setSize(w, h);
		final BufferedImage image = bot.getBufferImage();
		if (image.getWidth() != w || image.getHeight() != h) {
			bot.resize(w, h);
		}
	}
}