package org.powerbot.bot.rt6.activation;

import java.awt.Color;
import java.awt.Graphics;

/**
 */
class DebugHelper {
	public static void drawLine(final Graphics render, final int row, final String text) {
		final int height = render.getFontMetrics().getHeight() + 4;
		final int x = 7, y = row * height + height + 19 + 50;
		render.setColor(Color.GREEN);
		render.drawString(text, x, y);
	}
}
