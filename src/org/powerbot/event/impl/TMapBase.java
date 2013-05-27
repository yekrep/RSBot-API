package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.util.StringUtil;

public class TMapBase implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Tile t = Game.getMapBase();
		StringUtil.drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
