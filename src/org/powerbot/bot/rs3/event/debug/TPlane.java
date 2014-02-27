package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

/**
 */
public class TPlane implements TextPaintListener {
	private final MethodContext ctx;

	public TPlane(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Floor: " + ctx.game.getPlane());
		return idx;
	}
}
