package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Tile;
import org.powerbot.util.StringUtil;

public class TLocation implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Player player = Players.getLocal();
		if (player != null) {
			final Tile tile = player.getLocation();
			StringUtil.drawLine(render, idx++, "Position: " + (tile != null ? tile.toString() : ""));
		}
		return idx;
	}
}
