package org.powerbot.bot.rt6;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TDestination implements TextPaintListener {
	private final ClientContext ctx;

	public TDestination(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Tile dest = ctx.movement.destination();
		drawLine(render, idx++, "Destination: " + (dest != null ? dest.toString() : "null"));
		return idx;
	}
}
