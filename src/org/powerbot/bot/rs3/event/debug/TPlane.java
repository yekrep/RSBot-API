package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rs3.tools.ClientContext;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

/**
 */
public class TPlane implements TextPaintListener {
	private final ClientContext ctx;

	public TPlane(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Floor: " + ctx.game.getPlane());
		return idx;
	}
}
