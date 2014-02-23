package org.powerbot.bot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.bot.event.debug.DebugHelper.drawLine;

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
