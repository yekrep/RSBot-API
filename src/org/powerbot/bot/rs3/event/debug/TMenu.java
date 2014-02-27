package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TMenu implements TextPaintListener {
	private final MethodContext ctx;

	public TMenu(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.getItems();
		for (final String menuItem : menuItems) {
			drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}