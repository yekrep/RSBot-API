package org.powerbot.bot.rt6.activation;

import java.awt.Graphics;

import org.powerbot.script.PaintListener;
import org.powerbot.script.TextPaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Projectile;
import org.powerbot.script.rt6.TileMatrix;

public class DrawProjectiles implements PaintListener, TextPaintListener {
	private final ClientContext ctx;

	public DrawProjectiles(final ClientContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}

		for (final Projectile projectile : ctx.projectiles.select()) {
			final Tile t = projectile.tile();
			final TileMatrix m = new TileMatrix(ctx, t);
			if (!m.valid()) {
				continue;
			}

			m.draw(render);
		}
	}

	@Override
	public int draw(int idx, final Graphics render) {
		DebugHelper.drawLine(render, idx++, "Projectile count: " + ctx.projectiles.size());
		return idx;
	}
}
