package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.event.debug.DebugHelper.drawLine;

public class TMenu implements TextPaintListener {
	protected final MethodContext ctx;

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