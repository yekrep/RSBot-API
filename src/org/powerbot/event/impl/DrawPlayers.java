package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Player;

public class DrawPlayers implements PaintListener {
	public void onRepaint(final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		for (final Player player : ctx.players.select()) {
			final Point location = player.getCenterPoint();
			if (location.x == -1 || location.y == -1) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = player.getName() + " (" + player.getLevel() + ")";
			render.setColor(player.isInCombat() ? Color.RED : player.isInMotion() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = player.getMessage();
			boolean raised = false;
			if (player.getAnimation() != -1 || player.getStance() != -1 || player.getNpcId() != -1 || player.getPrayerIcon() != -1 || player.getSkullIcon() != -1) {
				s = "";
				s += "(";
				if (player.getNpcId() != -1) {
					s += "NPC: " + player.getNpcId() + " | ";
				}
				if (player.getPrayerIcon() != -1) {
					s += "P: " + player.getPrayerIcon() + " | ";
				}
				if (player.getSkullIcon() != -1) {
					s += "SK: " + player.getSkullIcon() + " | ";
				}
				if (player.getAnimation() != -1 || player.getStance() > 0) {
					s += "A: " + player.getAnimation() + " | ST: " + player.getStance() + " | ";
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