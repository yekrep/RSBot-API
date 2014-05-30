package org.powerbot.bot;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.script.TextPaintListener;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TMousePosition<C extends ClientContext> extends ClientAccessor<C> implements TextPaintListener {

	public TMousePosition(final C ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		final Point p = ctx.input.getLocation();
		drawLine(render, idx++, "Mouse position: " + (int) p.getX() + "," + (int) p.getY());
		return idx;
	}
}
