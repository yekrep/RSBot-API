package org.powerbot.game.bot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.powerbot.game.api.methods.Players;
import org.powerbot.game.api.wrappers.GameModel;
import org.powerbot.game.api.wrappers.Player;
import org.powerbot.game.bot.event.listener.PaintListener;

public class DrawModels implements PaintListener {
	public void onRepaint(final Graphics graphics) {
		Graphics2D render = (Graphics2D) graphics;
		render.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		render.setColor(Color.green);
		final Player[] players = Players.getLoaded();
		for (final Player player : players) {
			final GameModel model = player.getModel();
			if (model != null) {
				model.draw(render);
			}
		}
	}
}
