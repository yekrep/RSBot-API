package org.powerbot.bot.rt6;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Npc;

public class DrawMobs extends ClientAccessor implements PaintListener {

	public DrawMobs(final ClientContext ctx) {
		super(ctx);
	}

	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
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
			String s = npc.name() + " (" + npc.combatLevel() + " [" + npc.healthPercent() + "]) - " + npc.id();
			render.setColor(npc.inCombat() ? Color.RED : npc.inMotion() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = npc.overheadMessage();
			boolean raised = false;
			if (npc.animation() != -1 || npc.stance() != -1) {
				s = "";
				s += "(";
				if (npc.prayerIcon() != -1) {
					s += "P: " + npc.prayerIcon() + " | ";
				}
				if (npc.animation() != -1 || npc.stance() > 0) {
					s += "A: " + npc.animation() + " | ST: " + npc.stance() + " | ";
				}
				if (s.contains(" | ")) {
					s = s.substring(0, s.lastIndexOf(" | "));
				}
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