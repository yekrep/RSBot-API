package org.powerbot.client.event.debug;

import org.powerbot.client.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Game;
import org.powerbot.util.StringUtil;

import java.awt.Graphics;

/**
 * @author Timer
 */
public class TCurrentTab implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		int tab = ctx.game.getCurrentTab();
		StringUtil.drawLine(render, idx++, "Tab: " + (tab == -1 ? "NONE" : Game.TAB_NAMES[tab]));
		return idx;
	}
}
