package org.powerbot.bot.rt6;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TMenu extends ClientAccessor implements TextPaintListener {
	public TMenu(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.items();
		for (final String menuItem : menuItems) {
			drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}