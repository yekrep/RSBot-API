package org.powerbot.bot.os.event.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.PaintListener;
import org.powerbot.script.os.ClientContext;
import org.powerbot.script.os.Game;
import org.powerbot.script.os.GroundItem;
import org.powerbot.script.os.Player;
import org.powerbot.script.os.Tile;

public class DrawGroundItems implements PaintListener {
	private final ClientContext ctx;

	public DrawGroundItems(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public void repaint(final Graphics render) {
		if (ctx.game.clientState() != Game.INDEX_MAP_LOADED) {
			return;
		}

		final Player player = ctx.players.local();
		if (player == null) {
			return;
		}
		final Tile tile = player.tile();
		if (tile == null) {
			return;
		}

		final FontMetrics metrics = render.getFontMetrics();
		final int tHeight = metrics.getHeight();
		final int plane = ctx.game.floor();
		final List<GroundItem> check = new ArrayList<GroundItem>();
		ctx.groundItems.select().addTo(check);
		for (int x = tile.x() - 10; x <= tile.x() + 10; x++) {
			for (int y = tile.y() - 10; y <= tile.y() + 10; y++) {
				int d = 0;
				final Tile loc = new Tile(x, y, plane);
				final Point screen = loc.matrix(ctx).centerPoint();
				if (screen.x == -1 || screen.y == -1) {
					continue;
				}
				for (final GroundItem groundItem : ctx.groundItems.select(check).at(loc)) {
					final String name = groundItem.name();
					String s = "";
					s += groundItem.id();
					if (!name.isEmpty()) {
						s += " " + name;
					}
					final int stack = groundItem.stackSize();
					if (stack > 1) {
						s += " (" + stack + ")";
					}
					final int ty = screen.y - tHeight * (++d) + tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.green);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}
