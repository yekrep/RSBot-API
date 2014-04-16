package org.powerbot.bot.rt4.activation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

/**
 */
public class ViewMouseTrails extends ClientAccessor implements PaintListener {
	private static final Deque<Point> h = new LinkedList<Point>();

	public ViewMouseTrails(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void repaint(final Graphics g) {
		final Point p = ctx.mouse.getLocation();
		if (p.x == -1 && p.y == -1) {
			return;
		}

		h.offerFirst(p);
		if (h.size() < 3) {
			return;
		}

		final Graphics2D g2 = (Graphics2D) g;
		final double u = 10;
		int i = -1;
		final Iterator<Point> e = h.iterator();
		Point a = e.next();

		while (e.hasNext() && ++i < u) {
			final Point b = e.next();
			g2.setColor(new Color(255, 255, 255, 200 - (int) (i / u * 200d)));
			g2.drawLine(a.x, a.y, b.x, b.y);
			a = b;
		}

		if (i == u) {
			h.pollLast();
		}
	}
}
