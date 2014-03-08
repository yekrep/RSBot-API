package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rs3.ClientContext;
import org.powerbot.script.rs3.Tile;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TDestination implements TextPaintListener {
	private final ClientContext ctx;

	public TDestination(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Tile dest = ctx.movement.getDestination();
		drawLine(render, idx++, "Destination: " + (dest != null ? dest.toString() : "null"));
		return idx;
	}
}
