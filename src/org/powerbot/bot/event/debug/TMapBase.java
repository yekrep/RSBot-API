package org.powerbot.bot.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.tools.MethodContext;
import org.powerbot.script.tools.Tile;

import static org.powerbot.bot.event.debug.DebugHelper.drawLine;

public class TMapBase implements TextPaintListener {
	private final MethodContext ctx;

	public TMapBase(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Tile t = ctx.game.getMapBase();
		drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
