package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;
import org.powerbot.script.rs3.tools.Tile;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TDestination implements TextPaintListener {
	private final MethodContext ctx;

	public TDestination(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Tile dest = ctx.movement.getDestination();
		drawLine(render, idx++, "Destination: " + (dest != null ? dest.toString() : "null"));
		return idx;
	}
}
