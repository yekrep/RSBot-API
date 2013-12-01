package org.powerbot.client.input;

import java.awt.AWTEvent;

import org.powerbot.bot.EventCallback;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -8630774417757577975L;

	public Canvas() {
		super();
		final SelectiveEventQueue queue = SelectiveEventQueue.getInstance();
		SelectiveEventQueue.pushSelectiveQueue();

		queue.block(this, new EventCallback() {
			@Override
			public void execute(final AWTEvent event) {
			}
		});
		if (queue.isBlocking()) {
			queue.focus();
		}

		BotChrome.getInstance().getBot().initiate();
	}
}