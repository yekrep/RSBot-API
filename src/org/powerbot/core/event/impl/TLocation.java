package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.script.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TLocation implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Player player = Players.getLocal();
		if (player != null) {
			final Tile tile = player.getLocation();
			StringUtil.drawLine(render, idx++, "Position: " + tile.toString());
		}
		return idx;
	}
}
