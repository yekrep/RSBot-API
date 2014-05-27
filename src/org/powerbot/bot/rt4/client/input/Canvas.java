package org.powerbot.bot.rt4.client.input;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.bot.rt4.Bot;
import org.powerbot.bot.rt4.activation.PaintEvent;
import org.powerbot.bot.rt4.activation.TextPaintEvent;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private final Bot bot;
	private BufferedImage game, clean;

	public Canvas() {
		final BotChrome chrome = BotChrome.getInstance();
		bot = (Bot) chrome.bot.get();
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		SelectiveEventQueue.pushSelectiveQueue();
		final SelectiveEventQueue queue = SelectiveEventQueue.getInstance();
		queue.target(this, new SelectiveEventQueue.EventCallback() {
			@Override
			public void execute(final AWTEvent event) {
				chrome.requestFocusInWindow();
			}
		});
		final InputSimulator s = queue.getEngine();
		if (s != null) {
			s.focus();
		}
	}

	@Override
	public Graphics getGraphics() {
		//Snapshot the game before painting.
		clean.getGraphics().drawImage(game, 0, 0, null);
		//Paint onto the game's image for displaying purposes.
		final Graphics g = game.getGraphics();
		final EventDispatcher m = bot.dispatcher;
		paintEvent.graphics = g;
		textPaintEvent.graphics = g;
		textPaintEvent.index = 0;
		try {
			m.consume(paintEvent);
			m.consume(textPaintEvent);
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		//Display the painted-on game image onto the canvas.
		super.getGraphics().drawImage(game, 0, 0, null);
		//Reset the game to the original clean image so the engine updates it correctly.
		game.getGraphics().drawImage(clean, 0, 0, null);
		//Return our game image's graphics so the game updates it for us.
		return g;
	}

	@Override
	public void setSize(final int width, final int height) {
		super.setSize(width, height);
		//Keep the images in-line with the size of the component.
		if (game == null || game.getWidth() != width || game.getHeight() != height) {
			if (width <= 0 || height <= 0) {
				return;
			}
			game = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}
}
