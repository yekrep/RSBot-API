package org.powerbot.bot.rt6;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

/**
 */
public class TPlane extends ClientAccessor implements TextPaintListener {

	public TPlane(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Floor: " + ctx.game.floor());
		return idx;
	}
}
