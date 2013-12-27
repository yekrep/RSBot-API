package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Projectile;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TileMatrix;

public class DrawProjectiles implements PaintListener {
	protected final MethodContext ctx;

	public DrawProjectiles(final MethodContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void repaint(Graphics render) {
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
}
