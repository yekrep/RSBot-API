package org.powerbot.bot.rt4.client.input;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.rt4.Bot;
import org.powerbot.script.PaintEvent;
import org.powerbot.script.TextPaintEvent;
import org.powerbot.gui.BotLauncher;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private final Bot bot;
	private BufferedImage game, clean;

	public Canvas() {
		final BotLauncher launcher = BotLauncher.getInstance();
		bot = (Bot) launcher.bot.get();
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		bot.ctx.input.focus();
	}

	@Override
	public Graphics getGraphics() {
		//Snapshot the game before painting.
		clean.getGraphics().drawImage(game, 0, 0, null);
		//Paint onto the game's image for displaying purposes.
		final Graphics g = game.getGraphics();
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
