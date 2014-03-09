package org.powerbot.bot.os.event.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.os.ClientContext;
import org.powerbot.script.os.Game;
import org.powerbot.script.os.Player;

public class DrawPlayers implements PaintListener {
	private final ClientContext ctx;

	public DrawPlayers(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public void repaint(final Graphics render) {
		if (ctx.game.clientState() != Game.INDEX_MAP_LOADED) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		for (final Player player : ctx.players.select()) {
			final Point location = player.centerPoint();
			if (location.x == -1 || location.y == -1) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = player.name() + " (" + player.combatLevel() + ")";
			render.setColor(false ? Color.RED : player.inMotion() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = player.overheadMessage();
			boolean raised = false;
			if (player.animation() != -1) {
				s = "";
				s += "(";
				if (player.animation() != -1) {
					s += "A: " + player.animation() + " | ST: -1 | ";
				}

				s = s.substring(0, s.lastIndexOf(" | "));
				s += ")";

				render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() * 3 / 2);
				raised = true;
			}
			if (msg != null) {
				render.setColor(Color.ORANGE);
				render.drawString(msg, location.x - metrics.stringWidth(msg) / 2, location.y - metrics.getHeight() * (raised ? 5 : 3) / 2);
			}
		}
	}
}