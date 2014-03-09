package org.powerbot.bot.os.event.debug;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.os.ClientContext;

import static org.powerbot.bot.os.event.debug.DebugHelper.drawLine;

/**
 */
public class TFloor implements TextPaintListener {
	private final ClientContext ctx;

	public TFloor(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Floor: " + ctx.game.floor());
		return idx;
	}
}
