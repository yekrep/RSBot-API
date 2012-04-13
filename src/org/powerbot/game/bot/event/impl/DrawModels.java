package org.powerbot.game.bot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;

import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.node.Location;
import org.powerbot.game.bot.event.listener.PaintListener;

public class DrawModels implements PaintListener {
	private static final HashMap<Location.Type, Color> color_map = new HashMap<Location.Type, Color>();

	static {
		color_map.put(Location.Type.BOUNDARY, Color.BLACK);
		color_map.put(Location.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(Location.Type.INTERACTIVE, Color.WHITE);
		color_map.put(Location.Type.WALL_DECORATION, Color.GRAY);
	}

	public void onRepaint(final Graphics graphics) {
		final Graphics2D render = (Graphics2D) graphics;
		render.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (final Location e : Locations.getLoaded()) {
			final CapturedModel model = e.getModel();
			if (model != null && e.getLocation().isOnScreen()) {
				render.setColor(color_map.get(e.getType()));
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 2, p.y - 2, 4, 4);
			}
		}
		for (final org.powerbot.game.api.wrappers.interactive.Character e : Players.getLoaded()) {
			final CapturedModel model = e.getModel();
			if (model != null && e.getLocation().isOnScreen()) {
				render.setColor(Color.red);
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 2, p.y - 2, 4, 4);
			}
		}
		for (final org.powerbot.game.api.wrappers.interactive.Character e : NPCs.getLoaded()) {
			final CapturedModel model = e.getModel();
			if (model != null && e.getLocation().isOnScreen()) {
				render.setColor(Color.magenta);
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 2, p.y - 2, 4, 4);
			}
		}
	}
}