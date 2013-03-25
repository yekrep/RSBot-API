package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Set;

import org.powerbot.event.PaintListener;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.GroundItems;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.wrappers.GroundItem;
import org.powerbot.script.xenon.wrappers.Item;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Tile;

public class DrawGroundItems implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) return;

		final Player player = Players.getLocal();
		if (player == null) return;
		final Tile tile = player.getLocation();
		if (tile == null) return;
		final FontMetrics metrics = render.getFontMetrics();
		final int tHeight = metrics.getHeight();
		final int plane = Game.getPlane();
		for (int x = tile.getX(); x <= tile.getX() + 20; x++)
			for (int y = tile.getY() - 10; y <= tile.getY() + 20; y++) {
				final Set<GroundItem> groundItems = GroundItems.getLoaded(x, y, 0);
				int d = 0;
				final Tile loc = new Tile(x, y, plane);
				final Point screen = loc.getCenterPoint();
				if (screen.x == -1 || screen.y == -1) continue;
				for (final GroundItem groundItem : groundItems) {
					final Item item = groundItem.getItem();
					final String name = item.getName();
					String s = "";
					if (name != null) s += name + " ";
					s += item.getId();
					final int stack = item.getStackSize();
					if (stack > 0) s += " (" + item.getStackSize() + ")";
					final int ty = screen.y - tHeight * (++d) + tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.green);
					render.drawString(s, tx, ty);
				}
			}
	}
}
