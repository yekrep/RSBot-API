package org.powerbot.bot.rt6.client.input;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.rt6.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.PaintEvent;
import org.powerbot.script.TextPaintEvent;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private BufferedImage real, clean;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private final BotChrome chrome;

	public Canvas() {
		chrome = BotChrome.getInstance();
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
	}

	@Override
	public Graphics getGraphics() {
		final Bot bot;

		// only use this buffering on safe mode where overlay is not supported
		if (chrome.overlay.get() != null || (bot = (Bot) chrome.bot.get()).ctx.game.toolkit.gameMode != 0) {
			return super.getGraphics();
		}

		if (clean == null || real == null) {
			return super.getGraphics();
		}

		//First and foremost, we need to keep our hands on a clean copy.
		//Store the clean game image via draw.
		final Graphics temp;
		(temp = clean.getGraphics()).drawImage(real, 0, 0, null);
		temp.dispose();

		//Now, we can get the graphics of the real (replaced) image we're working with to draw on.
		//This was wiped clean by being returned and painted on by the game engine.
		final Graphics g = real.getGraphics();
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final EventDispatcher m = bot.dispatcher;
		paintEvent.graphics = g;
		textPaintEvent.graphics = g;
		textPaintEvent.index = 0;
		try {
			m.consume(paintEvent);
			m.consume(textPaintEvent);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		//Paint our image onto the original graphics so it is displayed to the user.
		super.getGraphics().drawImage(real, 0, 0, null);
		//Give the game our graphics to give us a clean slate (this is what the game paints to).
		//Perhaps we should do g.drawImage(real, 0, 0, null) to give
		//the engine a blank image again after we painted with it to avoid
		//detection via raster sniffing.  Just a thought.
		return g;
	}

	@Override
	public void setSize(final int width, final int height) {
		super.setSize(width, height);
		//Keep the images in-line with the size of the component.
		if (real == null || real.getWidth() != width || real.getHeight() != height) {
			if (width <= 0 || height <= 0) {
				return;
			}
			real = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}
}