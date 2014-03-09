package org.powerbot.bot.os.event.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.os.ClientContext;
import org.powerbot.script.os.Game;
import org.powerbot.script.os.GameObject;
import org.powerbot.script.os.Player;
import org.powerbot.script.os.Tile;

public class DrawObjects implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private final ClientContext ctx;

	public DrawObjects(final ClientContext ctx) {
		this.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void repaint(final Graphics render) {
		if (ctx.game.clientState() != Game.INDEX_MAP_LOADED) {
			return;
		}
		final Player player = ctx.players.local();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final int textHeight = metrics.getHeight();
		for (final GameObject object : ctx.objects.select().within(25)) {
			final Tile t = object.tile();
			if (t == null) {
				continue;
			}

			Point p = t.matrix(ctx).centerPoint();
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
			//render.setColor(C[object.type()]);
			render.setColor(Color.green);
			render.drawString(s, tx, ty);
		}
	}
}