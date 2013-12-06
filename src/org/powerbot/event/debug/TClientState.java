package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.event.debug.DebugHelper.drawLine;

/**
 * @author Timer
 */
public class TClientState implements TextPaintListener {
	protected final MethodContext ctx;

	public TClientState(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Client state: " + ctx.game.getClientState());
		return idx;
	}
}
