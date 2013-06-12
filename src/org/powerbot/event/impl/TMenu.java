package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

import java.awt.*;

public class TMenu implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		StringUtil.drawLine(render, idx++, "Menu");
		final String[] menuItems = ctx.menu.getItems();
		for (final String menuItem : menuItems) {
			StringUtil.drawLine(render, idx++, " -> " + menuItem);
		}
		return idx;
	}
}