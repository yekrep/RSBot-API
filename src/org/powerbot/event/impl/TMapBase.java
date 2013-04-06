package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TMapBase implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++, "Map base: " + new Tile(Game.getBaseX(), Game.getBaseY(), -1).toString());
		return idx;
	}
}
