package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Tile;

import static org.powerbot.event.debug.DebugHelper.drawLine;

public class TDestination implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		final Tile dest = ctx.movement.getDestination();
		drawLine(render, idx++, "Destination: " + (dest != null ? dest.toString() : "null"));
		return idx;
	}
}
