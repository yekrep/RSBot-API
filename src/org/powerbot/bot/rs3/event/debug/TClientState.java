package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

/**
 * @author Timer
 */
public class TClientState implements TextPaintListener {
	private final MethodContext ctx;

	public TClientState(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Client state: " + ctx.game.getClientState());
		return idx;
	}
}
