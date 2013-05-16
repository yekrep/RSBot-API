package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.powerbot.event.PaintListener;
import org.powerbot.script.xenon.GroundItems;
import org.powerbot.script.xenon.Npcs;
import org.powerbot.script.xenon.Objects;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.wrappers.GameObject;
import org.powerbot.script.xenon.wrappers.GroundItem;
import org.powerbot.script.xenon.wrappers.Model;
import org.powerbot.script.xenon.wrappers.Npc;
import org.powerbot.script.xenon.wrappers.Player;

public class DrawModels implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};

	@Override
	public void onRepaint(final Graphics render) {
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final GameObject[] objects = Objects.getLoaded(10);
		for (final GameObject obj : objects) {
			if (!obj.isOnScreen()) continue;
			final Model m = obj.getModel();
			if (m == null) continue;
			final int o = obj.getType().ordinal();
			final int rgb = C[o].getRGB();
			render.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, A[o]));
			m.drawWireFrame(render);
		}

		final Player[] players = Players.getLoaded();
		for (final Player actor : players) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 5);
		}

		final Npc[] npcs = Npcs.getLoaded();
		for (final Npc actor : npcs) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 20);
		}

		final GroundItem[] groundItems = GroundItems.getLoaded(20);
		for (final GroundItem item : groundItems) {
			item.draw(render, 20);
		}
	}
}