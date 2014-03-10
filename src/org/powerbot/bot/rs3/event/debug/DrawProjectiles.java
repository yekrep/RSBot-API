package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.PaintListener;
import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rs3.ClientContext;
import org.powerbot.script.rs3.Projectile;
import org.powerbot.script.rs3.Tile;
import org.powerbot.script.rs3.TileMatrix;

public class DrawProjectiles implements PaintListener, TextPaintListener {
	private final ClientContext ctx;

	public DrawProjectiles(final ClientContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		for (final Projectile projectile : ctx.projectiles.select()) {
			final Tile t = projectile.getLocation();
			final TileMatrix m = t.getMatrix(ctx);
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
