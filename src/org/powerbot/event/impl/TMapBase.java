package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.util.StringUtil;

import java.awt.*;

public class TMapBase implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		final Tile t = ctx.game.getMapBase();
		StringUtil.drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
