package org.powerbot.event.impl;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Model;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DrawModels implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};

	@Override
	public void onRepaint(final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (final GameObject obj : ctx.objects.select().within(10)) {
			if (!obj.isOnScreen()) {
				continue;
			}
			final Model m = obj.getModel();
			if (m == null) {
				continue;
			}
			final int o = obj.getType().ordinal();
			final int rgb = C[o].getRGB();
			render.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, A[o]));
			m.drawWireFrame(render);
		}

		for (final Player actor : ctx.players.select()) {
			if (!actor.isOnScreen()) {
				continue;
			}
			actor.draw(render, 10);
		}

		for (final Npc actor : ctx.npcs.select()) {
			if (!actor.isOnScreen()) {
				continue;
			}
			actor.draw(render, 20);
		}

		for (final GroundItem item : ctx.groundItems.select().within(20)) {
			item.draw(render, 20);
		}
	}
}