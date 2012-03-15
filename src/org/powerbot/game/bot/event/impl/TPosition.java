package org.powerbot.game.bot.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Players;
import org.powerbot.game.api.wrappers.Player;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.event.listener.internal.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TPosition implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		Player player = Players.getLocal();
		if (player != null) {
			StringUtil.drawLine(render, idx++, new StringBuilder("[green]Position: ").append(new Tile(Game.getBaseX(), Game.getBaseY()).toString()).toString());
		}
		return idx;
	}
}
