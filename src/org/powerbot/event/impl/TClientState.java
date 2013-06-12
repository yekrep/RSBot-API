package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.bot.Bot;
import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

/**
 * @author Timer
 */
public class TClientState implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		StringUtil.drawLine(render, idx++, "Client state: " + ctx.game.getClientState());
		return idx;
	}
}
