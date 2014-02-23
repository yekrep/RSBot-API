package org.powerbot.bot.event.debug;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.bot.event.debug.DebugHelper.drawLine;

public class TMousePosition implements TextPaintListener {
	private final MethodContext ctx;

	public TMousePosition(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		final Point p = ctx.mouse.getLocation();
		drawLine(render, idx++, "Mouse position: " + (int) p.getX() + "," + (int) p.getY());
		return idx;
	}
}
