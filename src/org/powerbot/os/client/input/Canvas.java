package org.powerbot.os.client.input;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.os.bot.Bot;
import org.powerbot.os.bot.EventCallback;
import org.powerbot.os.bot.SelectiveEventQueue;
import org.powerbot.os.bot.event.EventDispatcher;
import org.powerbot.os.bot.event.PaintEvent;
import org.powerbot.os.gui.BotChrome;

@SuppressWarnings("unused")
public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private final PaintEvent paintEvent;
	private final Bot bot;
	private BufferedImage game, clean;

	public Canvas() {
		final BotChrome chrome = BotChrome.getInstance();
		bot = chrome.bot.get();
		paintEvent = new PaintEvent();
		SelectiveEventQueue.getInstance().block(this, new EventCallback() {
			@Override
			public void execute(final AWTEvent event) {
				chrome.requestFocusInWindow();
			}
		});
	}

	@Override
	public Graphics getGraphics() {
		//Snapshot the game before painting.
		clean.getGraphics().drawImage(game, 0, 0, null);
		//Paint onto the game's image for displaying purposes.
		final Graphics g = game.getGraphics();
		final EventDispatcher m = bot.dispatcher;
		paintEvent.graphics = g;
		try {
			m.consume(paintEvent);
		} catch (final Exception e) {
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
			game = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}
}