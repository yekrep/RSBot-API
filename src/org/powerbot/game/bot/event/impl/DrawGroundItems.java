package org.powerbot.game.bot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.GroundItems;
import org.powerbot.game.api.methods.Players;
import org.powerbot.game.api.wrappers.GroundItem;
import org.powerbot.game.api.wrappers.ItemDefinition;
import org.powerbot.game.api.wrappers.Player;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.event.listener.PaintListener;

public class DrawGroundItems implements PaintListener {
	public void onRepaint(final Graphics render) {
		try {
			if (!Game.isLoggedIn()) {
				return;
			}
			final Player player = Players.getLocal();
			if (player == null) {
				return;
			}
			final Tile location = player.getLocation();
			final FontMetrics metrics = render.getFontMetrics();
			final int tHeight = metrics.getHeight();
			final int lX = location.x, lY = location.y;
			for (int x = lX - 25; x < lX + 25; x++) {
				for (int y = lY - 25; y < lY + 25; y++) {
					final GroundItem[] groundItems = GroundItems.getLoadedAt(x, y);
					int i = 0;
					for (final GroundItem groundItem : groundItems) {
						final Tile itemLocation = groundItem.getLocation();
						final Point screen = itemLocation.getCentralPoint();
						if (screen.x == -1 || screen.y == -1) {
							continue;
						}
						render.setColor(Color.red);
						render.fillRect((int) screen.getX() - 1, (int) screen.getY() - 1, 2, 2);
						final ItemDefinition itemDefinition = groundItem.getGroundItem().getDefinition();
						final StringBuilder sB = new StringBuilder(itemDefinition != null ? itemDefinition.getName() : "");
						if (itemDefinition != null) {
							sB.append(' ');
						}
						sB.append(groundItem.getGroundItem().getId()).append(" (").append(groundItem.getGroundItem().getStackSize()).append(')');
						final String s = sB.toString();
						final int ty = screen.y - tHeight * (++i) + tHeight / 2;
						final int tx = screen.x - metrics.stringWidth(s) / 2;
						render.setColor(Color.green);
						render.drawString(s, tx, ty);

					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
