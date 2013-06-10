package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.World;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.util.StringUtil;

public class TLocation implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Player player = World.getPlayer();
		if (player != null) {
			final Tile tile = player.getLocation();
			StringUtil.drawLine(render, idx++, "Position: " + (tile != null ? tile.toString() : ""));
		}
		return idx;
	}
}
