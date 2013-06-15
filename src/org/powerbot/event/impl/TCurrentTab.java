package org.powerbot.event.impl;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.Game;
import org.powerbot.util.StringUtil;

import java.awt.Graphics;

/**
 * @author Timer
 */
public class TCurrentTab implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		int tab = ctx.game.getCurrentTab();
		StringUtil.drawLine(render, idx++, "Tab: " + (tab == -1 ? "NONE" : Game.TAB_NAMES[tab]));
		return idx;
	}
}
