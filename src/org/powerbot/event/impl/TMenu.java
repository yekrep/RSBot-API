package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

public class TMenu implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		StringUtil.drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.getItems();
		for (final String menuItem : menuItems) {
			StringUtil.drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}