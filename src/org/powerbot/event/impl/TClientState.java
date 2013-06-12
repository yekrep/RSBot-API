package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

/**
 * @author Timer
 */
public class TClientState implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		StringUtil.drawLine(render, idx++, "Client state: " + ctx.game.getClientState());
		return idx;
	}
}
