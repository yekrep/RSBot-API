package org.powerbot.bot.rt4.activation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

public class DrawItems implements PaintListener {
	private final ClientContext ctx;

	public DrawItems(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}

		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);

		for (final Item item : ctx.inventory.select()) {
			final Point p = item.centerPoint();
			p.translate(-21, -18);
			render.drawString(item.id() + "", p.x, p.y + 36);
		}
	}
}
