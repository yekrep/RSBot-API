package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.util.StringUtil;

public class TMenu implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		StringUtil.drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.getItems();
		for (final String menuItem : menuItems) {
			StringUtil.drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}