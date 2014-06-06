package org.powerbot.bot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.script.PaintListener;

/**
 */
public class ViewMouseTrails<C extends ClientContext> extends ClientAccessor<C> implements PaintListener {
	private static final Deque<Point> h = new LinkedList<Point>();

	public ViewMouseTrails(final C ctx) {
		super(ctx);
	}

	@Override
	public void repaint(final Graphics g) {
		final Point p = ctx.input.getLocation();
		if (p.x == -1 || p.y == -1) {
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
