package org.powerbot.game.api;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.logging.Logger;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.event.listener.PaintListener;
import org.powerbot.lang.Activatable;

/**
 * @author Timer
 */
public abstract class AntiRandom implements Activatable, Task, PaintListener {
	public final Logger log = Logger.getLogger(getClass().getName());
	protected final Bot bot;

	public AntiRandom() {
		this.bot = Bot.resolve();
	}

	public void onRepaint(final Graphics render) {
		final Point p = Mouse.getLocation();
		final Canvas canvas = Bot.resolve().getCanvas();
		final int w = canvas.getWidth(), h = canvas.getHeight();
		render.setColor(new Color(51, 153, 255, 50));
		render.fillRect(0, 0, p.x - 1, p.y - 1);
		render.fillRect(p.x + 1, 0, w - (p.x + 1), p.y - 1);
		render.fillRect(0, p.y + 1, p.x - 1, h - (p.y - 1));
		render.fillRect(p.x + 1, p.y + 1, w - (p.x + 1), h - (p.y - 1));

		final Manifest manifest = getClass().getAnnotation(Manifest.class);
		final StringBuilder builder = new StringBuilder();
		builder.append(manifest.name()).append(" v").append(manifest.version());
		final String random = builder.toString();
		builder.setLength(0);
		final String description = manifest.description();
		final String[] authors = manifest.authors();
		final int length = authors.length;
		builder.append(length == 1 ? "Author: " : "Authors: ");
		if (length == 0) {
			builder.append("Unknown");
		} else {
			int index = 0;
			while (index < length) {
				builder.append(authors[index++]);
				builder.append(index < length ? ", " : "");
			}
		}
		final String creators = builder.toString();

		final FontMetrics metrics = render.getFontMetrics();
		//TODO paint random \n description \n creators
	}
}
