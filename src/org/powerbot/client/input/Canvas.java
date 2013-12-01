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

		bot.initiate();
	}
	}
}