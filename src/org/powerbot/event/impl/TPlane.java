package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

import java.awt.*;

/**
 * @author Timer
 */
public class TPlane implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		StringUtil.drawLine(render, idx++, "Floor: " + ctx.game.getPlane());
		return idx;
	}
}
