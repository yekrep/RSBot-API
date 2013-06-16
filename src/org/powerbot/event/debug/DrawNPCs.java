package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Npc;

public class DrawNPCs implements PaintListener {
	public void repaint(final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		for (final Npc npc : ctx.npcs.select()) {
			final Point location = npc.getCenterPoint();
			if (location.x == -1 || location.y == -1) {
				continue;
			}
			render.setColor(Color.red);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = npc.getName() + " (" + npc.getLevel() + ") - " + npc.getId();
			render.setColor(npc.isInCombat() ? Color.RED : npc.isInMotion() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = npc.getMessage();
			boolean raised = false;
			if (npc.getAnimation() != -1 || npc.getStance() != -1) {
				s = "";
				s += "(";
				if (npc.getAnimation() != -1 || npc.getStance() > 0) {
					s += "A: " + npc.getAnimation() + " | ST: " + npc.getStance() + " | ";
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