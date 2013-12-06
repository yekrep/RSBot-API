package org.powerbot.client.input;

import java.awt.AWTEvent;

import org.powerbot.bot.EventCallback;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.gui.BotChrome;

@SuppressWarnings("unused")
public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;

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