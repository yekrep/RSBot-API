package org.powerbot.bot.rt6;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.*;

import java.awt.*;

import static org.powerbot.bot.DebugHelper.*;

public class DrawProjectiles extends ClientAccessor implements PaintListener, TextPaintListener {
	public DrawProjectiles(final ClientContext ctx) {
		super(ctx);
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
			m.bounds(new int[]{-128, 128, -256, 0, -128, 128});
			m.draw(render, 255);
		}
	}

	@Override
	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Projectile count: " + ctx.projectiles.size());
		return idx;
	}
}
