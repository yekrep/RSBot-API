package org.powerbot.bot.os.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.os.ClientContext;
import org.powerbot.script.os.Tile;

import static org.powerbot.bot.os.event.debug.DebugHelper.drawLine;

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
