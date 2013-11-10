package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.event.debug.DebugHelper.drawLine;

public class TMenu implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.getItems();
		for (final String menuItem : menuItems) {
			drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}