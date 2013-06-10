package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.*;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Model;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Player;

public class DrawModels implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};

	@Override
	public void onRepaint(final Graphics render) {
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GameObject[] objects = Filters.range(World.getObjects(), 10);
		for (final GameObject obj : objects) {
			if (!obj.isOnScreen()) continue;
			final Model m = obj.getModel();
			if (m == null) continue;
			final int o = obj.getType().ordinal();
			final int rgb = C[o].getRGB();
			render.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, A[o]));
			m.drawWireFrame(render);
		}

		Player[] players = World.getPlayers();
		for (final Player actor : players) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 10);
		}

		Npc[] npcs = World.getNpcs();
		for (final Npc actor : npcs) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 20);
		}

		GroundItem[] groundItems = World.getStacks();
		groundItems = Filters.range(groundItems, 20);
		for (final GroundItem item : groundItems) {
			item.draw(render, 20);
		}
	}
}