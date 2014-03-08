package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rs3.tools.ClientContext;
import org.powerbot.script.rs3.tools.Tile;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TMapBase implements TextPaintListener {
	private final ClientContext ctx;

	public TMapBase(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Tile t = ctx.game.getMapBase();
		drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
