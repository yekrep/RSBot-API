package org.powerbot.bot.rt6.activation;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TMousePosition extends ClientAccessor implements TextPaintListener {

	public TMousePosition(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		final Point p = ctx.input.getLocation();
		drawLine(render, idx++, "Mouse position: " + (int) p.getX() + "," + (int) p.getY());
		return idx;
	}
}
