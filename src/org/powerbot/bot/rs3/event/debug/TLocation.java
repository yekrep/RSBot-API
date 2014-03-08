package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rs3.ClientContext;
import org.powerbot.script.rs3.Player;
import org.powerbot.script.rs3.Tile;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TLocation implements TextPaintListener {
	private final ClientContext ctx;

	public TLocation(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Player player = ctx.players.local();
		if (player != null) {
			final Tile tile = player.getLocation();
			drawLine(render, idx++, "Position: " + (tile != null ? tile.toString() : ""));
		}
		return idx;
	}
}
