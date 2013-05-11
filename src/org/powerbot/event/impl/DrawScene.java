package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import org.powerbot.event.PaintListener;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Objects;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.wrappers.GameObject;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Tile;

public class DrawScene implements PaintListener {
	private static final HashMap<GameObject.Type, Color> color_map = new HashMap<>();

	static {
		color_map.put(GameObject.Type.BOUNDARY, Color.BLACK);
		color_map.put(GameObject.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(GameObject.Type.INTERACTIVE, Color.WHITE);
		color_map.put(GameObject.Type.WALL_DECORATION, Color.GRAY);
	}

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
					render.setColor(color_map.get(location.getType()));
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}