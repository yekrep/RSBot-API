package org.powerbot.bot.rt4;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.HintArrow;
import org.powerbot.script.rt4.Player;
import org.powerbot.script.rt4.Projectile;

public class DrawProjectiles extends ClientAccessor implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE, Color.PINK};

	public DrawProjectiles(final ClientContext ctx) {
		super(ctx);
	}

	private Tile getTileBetween(Tile t1, Tile t2) {
		final int x1 = t1.x(), x2 = t2.x(), y1 = t1.y(), y2 = t2.y();
		return new Tile(x1 > x2 ? x1 - ((x1 - x2) / 2) : x2 - ((x2 - x1) / 2), y1 > y2 ? y1 - ((y1 - y2) / 2) : y2 - ((y2 - y1) / 2), ctx.client().getFloor());
	}

	@Override
	public void repaint(final Graphics render) {
		if (ctx.game.clientState() != Constants.GAME_LOADED) {
			return;
		}
		final Player player = ctx.players.local();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final int textHeight = metrics.getHeight();

		final Map<Tile, AtomicInteger> counts = new HashMap<Tile, AtomicInteger>();
		for (final Projectile o : ctx.projectiles.select()) {
			Tile t = getTileBetween(new Tile(o.getStartX(), o.getStartY(), o.getStartZ()), o.getTarget().tile());
			if (!counts.containsKey(t)) {
				counts.put(t, new AtomicInteger(0));
			}
			final Point p = t.matrix(ctx).centerPoint();
			if (p.x == -1) {
				continue;
			}

			render.setColor(Color.black);
			render.fillRect(p.x - 1, p.y - 1, 2, 2);

			final String s = String.format("[%s] INT: %s", o.id(),  o.getTarget());
			final int ty = p.y - textHeight / 2;
			final int tx = p.x - metrics.stringWidth(s) / 2;

			if(o.getTarget().equals(ctx.players.local())){
				render.setColor(C[4]);
			} else {
				render.setColor(C[0]);
			}
			render.drawString(s, tx, ty - textHeight * counts.get(t).getAndIncrement());

		}
	}
}
