package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.PaintListener;
import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;
import org.powerbot.script.rs3.tools.Projectile;
import org.powerbot.script.rs3.tools.Tile;
import org.powerbot.script.rs3.tools.TileMatrix;

public class DrawProjectiles implements PaintListener, TextPaintListener {
	private final MethodContext ctx;

	public DrawProjectiles(final MethodContext ctx) {
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
			if (!m.isValid()) {
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
