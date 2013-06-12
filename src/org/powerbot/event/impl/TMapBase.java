package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.util.StringUtil;

public class TMapBase implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		final Tile t = ctx.game.getMapBase();
		StringUtil.drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
