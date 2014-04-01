package org.powerbot.bot.rt4.activation;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.bot.rt4.activation.DebugHelper.drawLine;

public class TClientState implements TextPaintListener {
	private final ClientContext ctx;

	public TClientState(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Client state: " + ctx.game.clientState());
		return idx;
	}
}
