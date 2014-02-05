package org.powerbot.bot.event.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import org.powerbot.client.Client;
import org.powerbot.client.input.Mouse;
import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.MethodContext;

public class ViewMouse implements PaintListener {
	protected final MethodContext ctx;

	public ViewMouse(final MethodContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void repaint(final Graphics render) {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return;
		}

		final Graphics2D g2 = (Graphics2D) render;
		final Point p = mouse.getLocation();
		final int l = 6;

		g2.setColor(new Color(255, 200, 0, 180));
		g2.setStroke(new BasicStroke(2));
		g2.draw(new Line2D.Float(p.x - l, p.y - l, p.x + l, p.y + l));
		g2.draw(new Line2D.Float(p.x + l, p.y - l, p.x - l, p.y + l));

		if (System.currentTimeMillis() - mouse.getPressTime() < 1000) {
			final Point px = mouse.getPressLocation();
			g2.setColor(Color.RED);
			g2.drawLine(px.x - l, px.y - l, px.x + l, px.y + l);
			g2.drawLine(px.x + l, px.y - l, px.x - l, px.y + l);
		}
	}
}
