package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Objects;
import org.powerbot.script.methods.Players;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class DrawScene implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};

	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) {
			return;
		}
		final Player player = Players.getLocal();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final Tile position = player.getLocation();
		final int textHeight = metrics.getHeight();
		for (int x = position.getX() - 25; x < position.getX() + 25; x++) {
			for (int y = position.getY() - 25; y < position.getY() + 25; y++) {
				final Tile accessPosition = new Tile(x, y, Game.getPlane());
				final Point accessPoint = accessPosition.getCenterPoint();
				if (!Game.isPointOnScreen(accessPoint)) {
					continue;
				}
				final GameObject[] locations = Objects.getLoaded(x, y, 0);
				int i = 0;
				for (final GameObject location : locations) {
					final Point locationPoint = location.getLocation().getCenterPoint();
					if (!Game.isPointOnScreen(locationPoint)) {
						continue;
					}
					if (accessPoint.x > -1) {
						render.setColor(Color.GREEN);
						render.fillRect(accessPoint.x - 1, accessPoint.y - 1, 2, 2);
						render.setColor(Color.RED);
						render.drawLine(accessPoint.x, accessPoint.y, locationPoint.x, locationPoint.y);
					}
					final String s = "" + location.getId();
					final int ty = locationPoint.y - textHeight / 2 - i++ * 15;
					final int tx = locationPoint.x - metrics.stringWidth(s) / 2;
					render.setColor(C[location.getType().ordinal()]);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}