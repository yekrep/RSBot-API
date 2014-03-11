package org.powerbot.bot.os.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.os.ClientContext;
import org.powerbot.script.os.Player;

import static org.powerbot.bot.os.event.debug.DebugHelper.drawLine;

public class TLocation implements TextPaintListener {
	private final ClientContext ctx;

	public TLocation(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Player player = ctx.players.local();
		if (player != null) {
			final Tile tile = player.tile();
			drawLine(render, idx++, "Position: " + (tile != null ? tile.toString() : ""));
		}
		return idx;
	}
}
