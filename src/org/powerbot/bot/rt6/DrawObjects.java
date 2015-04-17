package org.powerbot.bot.rt6;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Player;
import org.powerbot.script.rt6.TileMatrix;

public class DrawObjects extends ClientAccessor implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE, Color.PINK};

	public DrawObjects(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}
		final Player player = ctx.players.local();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final int textHeight = metrics.getHeight();

		final Map<Tile, AtomicInteger> counts = new HashMap<Tile, AtomicInteger>();
		for (final GameObject object : ctx.objects.select().within(25)) {
			final Tile t = object.tile();
			if (t == null) {
				continue;
			}
			if (!counts.containsKey(t)) {
				counts.put(t, new AtomicInteger(0));
			}

			Point p = new TileMatrix(ctx, t).centerPoint();
			if (p.x == -1) {
				continue;
			}

			final Point p2 = p;
			p = object.centerPoint();
			if (p.x == -1) {
				continue;
			}

			render.setColor(Color.gray);
			render.fillRect(p2.x - 1, p2.y - 1, 2, 2);
			render.setColor(Color.black);
			render.fillRect(p.x - 1, p.y - 1, 2, 2);

			render.setColor(new Color(0, 0, 0, 100));
			render.drawLine(p.x, p.y, p2.x, p2.y);

			final String s = "" + object.id();
			final int ty = p.y - textHeight / 2;
			final int tx = p.x - metrics.stringWidth(s) / 2;
			render.setColor(C[object.type().ordinal()]);
			render.drawString(s, tx, ty - textHeight * counts.get(t).getAndIncrement());
		}
	}
}