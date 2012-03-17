package org.powerbot.game.bot.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Players;
import org.powerbot.game.api.wrappers.Player;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.event.listener.internal.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TPosition implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Player player = Players.getLocal();
		if (player != null) {
			final Tile tile = player.getLocation();
			StringUtil.drawLine(render, idx++, new StringBuilder("Position: ").append(new Tile(tile.x, tile.y).toString()).toString());
		}
		return idx;
	}
}
