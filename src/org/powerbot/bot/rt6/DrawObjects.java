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
		for (final GameObject object : ctx.objects.select(15)) {
			final Tile t = object.tile();
			if (!counts.containsKey(t)) {
				counts.put(t, new AtomicInteger(0));
			}

			final Point p = object.centerPoint();
			if (p.x == -1) {
				continue;
			}

			render.setColor(Color.black);
			render.fillRect(p.x - 1, p.y - 1, 2, 2);

			final int mainId = object.mainId();
			final int animation = object.animation();
			final String n = object.name();
			String s = (n.isEmpty() ? "" : n + " - ") + object.id();
			if (animation != -1) {
				s = s + " (A: " + animation + ")";
			}
			if (mainId != -1) {
				if (animation != -1) {
					s = s.replace(')', ',') + " MID: " + mainId + ")";
				} else {
					s = s + " (MID: " + mainId + ")";
				}
			}
			final int ty = p.y - textHeight / 2;
			final int tx = p.x - metrics.stringWidth(s) / 2;
			render.setColor(C[object.type().ordinal()]);
			render.drawString(s, tx, ty - textHeight * counts.get(t).getAndIncrement());
		}
	}
}