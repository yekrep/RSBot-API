package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.ItemDefinition;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;

public class DrawGroundItems implements PaintListener {
	public void onRepaint(final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		if (!ctx.game.isLoggedIn()) return;

		final Player player = ctx.players.getLocal();
		if (player == null) return;
		final Tile tile = player.getLocation();
		if (tile == null) return;
		final FontMetrics metrics = render.getFontMetrics();
		final int tHeight = metrics.getHeight();
		final int plane = ctx.game.getPlane();
		for (int x = tile.getX(); x <= tile.getX() + 20; x++) {
			for (int y = tile.getY() - 10; y <= tile.getY() + 20; y++) {
				GroundItem[] groundItems = ctx.groundItems.getLoaded();
				groundItems = Filters.at(groundItems, new Tile(ctx, x, y, ctx.game.getPlane()));
				int d = 0;
				final Tile loc = new Tile(ctx, x, y, plane);
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
