package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import static org.powerbot.event.debug.DebugHelper.drawLine;

public class TLocation implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		final Player player = ctx.players.local();
		if (player != null) {
			final Tile tile = player.getLocation();
			drawLine(render, idx++, "Position: " + (tile != null ? tile.toString() : ""));
		}
		return idx;
	}
}
