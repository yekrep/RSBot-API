package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;

public class DrawScene implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};

	public void onRepaint(final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final Player player = ctx.players.getLocal();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final Tile position = player.getLocation();
		final int textHeight = metrics.getHeight();
		for (int x = position.getX() - 25; x < position.getX() + 25; x++) {
			for (int y = position.getY() - 25; y < position.getY() + 25; y++) {
				final Tile accessPosition = new Tile(ctx, x, y, ctx.game.getPlane());
				final Point accessPoint = accessPosition.getCenterPoint();
				if (!ctx.game.isPointOnScreen(accessPoint)) {
					continue;
				}
				final GameObject[] locations = Filters.at(ctx.objects.getLoaded(), new Tile(ctx, x, y, ctx.game.getPlane()));
				int i = 0;
				for (final GameObject location : locations) {
					final Point locationPoint = location.getLocation().getCenterPoint();
					if (!ctx.game.isPointOnScreen(locationPoint)) {
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