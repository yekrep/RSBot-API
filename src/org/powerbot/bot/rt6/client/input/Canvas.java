package org.powerbot.bot.rt6.client.input;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.bot.rt6.Bot;
import org.powerbot.bot.rt6.activation.PaintEvent;
import org.powerbot.bot.rt6.activation.TextPaintEvent;
import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private BufferedImage real, clean;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private final Bot bot;

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
		// only use this buffering on safe mode where overlay is not supported
		if (bot.ctx.game.toolkit.gameMode != 0 || Boolean.parseBoolean(System.getProperty("swing.transparency", "true"))) {
			return super.getGraphics();
		}

		//First and foremost, we need to keep our hands on a clean copy.
		//Store the clean game image via draw.
		clean.getGraphics().drawImage(real, 0, 0, null);

		//Now, we can get the graphics of the real (replaced) image we're working with to draw on.
		//This was wiped clean by being returned and painted on by the game engine.
		final Graphics g = real.getGraphics();

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