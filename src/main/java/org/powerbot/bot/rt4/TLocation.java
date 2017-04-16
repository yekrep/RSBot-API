package org.powerbot.bot.rt4;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Player;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TLocation extends ClientAccessor implements TextPaintListener {

	public TLocation(final ClientContext ctx) {
		super(ctx);
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
