package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.World;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.ItemDefinition;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class DrawGroundItems implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) return;

		final Player player = World.getPlayer();
		if (player == null) return;
		final Tile tile = player.getLocation();
		if (tile == null) return;
		final FontMetrics metrics = render.getFontMetrics();
		final int tHeight = metrics.getHeight();
		final int plane = Game.getPlane();
		for (int x = tile.getX(); x <= tile.getX() + 20; x++) {
			for (int y = tile.getY() - 10; y <= tile.getY() + 20; y++) {
				GroundItem[] groundItems = World.getGroundItems();
				groundItems = Filters.at(groundItems, new Tile(x, y, Game.getPlane()));
				int d = 0;
				final Tile loc = new Tile(x, y, plane);
				final Point screen = loc.getCenterPoint();
				if (screen.x == -1 || screen.y == -1) continue;
				for (final GroundItem groundItem : groundItems) {
					final ItemDefinition def = groundItem.getDefinition();
					final String name = def != null ? def.getName() : null;
					String s = "";
					if (name != null) s += name + " ";
					s += groundItem.getId();
					final int stack = groundItem.getStackSize();
					if (stack > 0) s += " (" + groundItem.getStackSize() + ")";
					final int ty = screen.y - tHeight * (++d) + tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.green);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}
