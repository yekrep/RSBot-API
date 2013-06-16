package org.powerbot.event.debug;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.ItemDefinition;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Deque;

public class DrawGroundItems implements PaintListener {
	public void repaint(final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		final Player player = ctx.players.getLocal();
		if (player == null) {
			return;
		}
		final Tile tile = player.getLocation();
		if (tile == null) {
			return;
		}

		final FontMetrics metrics = render.getFontMetrics();
		final int tHeight = metrics.getHeight();
		final int plane = ctx.game.getPlane();
		Deque<GroundItem> groundItemDeque = ctx.groundItems.select().toDeque();
		for (int x = tile.getX() - 10; x <= tile.getX() + 10; x++) {
			for (int y = tile.getY() - 10; y <= tile.getY() + 10; y++) {
				int d = 0;
				final Tile loc = new Tile(x, y, plane);
				final Point screen = loc.getMatrix(ctx).getCenterPoint();
				if (screen.x == -1 || screen.y == -1) {
					continue;
				}
				for (final GroundItem groundItem : ctx.groundItems.select(groundItemDeque).at(loc)) {
					final ItemDefinition def = groundItem.getDefinition();
					final String name = def != null ? def.getName() : null;
					String s = "";
					s += groundItem.getId();
					if (name != null) {
						s += " " + name;
					}
					final int stack = groundItem.getStackSize();
					if (stack > 1) {
						s += " (" + groundItem.getStackSize() + ")";
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
