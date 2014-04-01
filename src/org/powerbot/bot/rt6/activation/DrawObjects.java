package org.powerbot.bot.rt6.activation;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.bot.rt6.client.RSAnimable;
import org.powerbot.bot.rt6.client.RSObject;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Player;
import org.powerbot.script.rt6.TileMatrix;

public class DrawObjects implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE, Color.PINK};
	private final ClientContext ctx;

	public DrawObjects(final ClientContext ctx) {
		this.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
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
		final Tile base = ctx.game.mapOffset();

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


			WeakReference<RSObject> internalObj;
			try {
				final Field f = object.getClass().getDeclaredField("object");
				f.setAccessible(true);
				internalObj = (WeakReference<RSObject>) f.get(object);
			} catch (final Exception ignored) {
				internalObj = null;
			}

			final RSObject rsObject = internalObj != null ? internalObj.get() : null;
			if (rsObject != null && rsObject instanceof RSAnimable) {
				final RSAnimable animable = (RSAnimable) rsObject;
				final int x1 = animable.getX1();
				final int x2 = animable.getX2();
				final int y1 = animable.getY1();
				final int y2 = animable.getY2();

				for (int _x = x1; _x <= x2; _x++) {
					for (int _y = y1; _y <= y2; _y++) {
						final Tile _tile = base.derive(_x, _y);
						new TileMatrix(ctx, _tile).draw(render);
					}
				}
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