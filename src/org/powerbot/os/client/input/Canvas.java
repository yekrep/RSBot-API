package org.powerbot.os.client.input;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.powerbot.os.bot.Bot;
import org.powerbot.os.bot.event.EventDispatcher;
import org.powerbot.os.bot.event.PaintEvent;
import org.powerbot.os.gui.BotChrome;

@SuppressWarnings("unused")
public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2284879212465893870L;
	private BufferedImage real, clean;
	private final PaintEvent paintEvent;
	private final Bot bot;

	public Canvas() {
		bot = BotChrome.getInstance().bot.get();
		paintEvent = new PaintEvent();
	}

	@Override
	public Graphics getGraphics() {
		//First and foremost, we need to keep our hands on a clean copy.
		//Store the clean game image via draw.
		clean.getGraphics().drawImage(real, 0, 0, null);

		//Now, we can get the graphics of the real (replaced) image we're working with to draw on.
		//This was wiped clean by being returned and painted on by the game engine.
		final Graphics g = real.getGraphics();

		final EventDispatcher m = bot.dispatcher;
		paintEvent.graphics = g;
		try {
			m.consume(paintEvent);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		//Paint our image onto the original graphics so it is displayed to the user.
		super.getGraphics().drawImage(real, 0, 0, null);
		//Give the game our graphics to give us a clean slate (this is what the game paints to).
		return g;
	}

	@Override
	public void setSize(final int width, final int height) {
		super.setSize(width, height);
		//Keep the images in-line with the size of the component.
		if (real == null || real.getWidth() != width || real.getHeight() != height) {
			real = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}
}