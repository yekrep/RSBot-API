package org.powerbot.bot.rt4;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

/**
 */
public class TFloor extends ClientAccessor implements TextPaintListener {

	public TFloor(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Floor: " + ctx.game.floor());
		return idx;
	}
}
