package org.powerbot.bot.rt4.activation;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;

public class DrawMobs implements PaintListener {
	private final ClientContext ctx;

	public DrawMobs(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public void repaint(final Graphics render) {
		if (ctx.game.clientState() != Game.INDEX_MAP_LOADED) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		for (final Npc npc : ctx.npcs.select()) {
			final Point location = npc.centerPoint();
			if (location.x == -1 || location.y == -1) {
				continue;
			}
			render.setColor(Color.red);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = npc.name() + " (" + npc.combatLevel() + ") - " + npc.id();
			render.setColor(false ? Color.RED : npc.inMotion() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = npc.overheadMessage();
			boolean raised = false;
			if (npc.animation() != -1) {
				s = "";
				s += "(";
				if (npc.animation() != -1) {
					s += "A: " + npc.animation() + " | ST: -1 | ";
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