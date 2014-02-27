package org.powerbot.bot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.tools.MethodContext;

import static org.powerbot.bot.event.debug.DebugHelper.drawLine;

/**
 * @author Timer
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
