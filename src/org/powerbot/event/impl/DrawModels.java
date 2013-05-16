package org.powerbot.event.impl;

import java.awt.AlphaComposite;
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
	private static final float[] A = {0.1f, 0.15f, 1f, 0.2f};

	@Override
	public void onRepaint(final Graphics render) {
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final GameObject[] objects = Objects.getLoaded(20);
		for (final GameObject obj : objects) {
			final Model m = obj.getModel();
			if (m == null) continue;
			final int o = obj.getType().ordinal();
			((Graphics2D) render).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, A[o]));
			render.setColor(C[o]);
			m.drawWireFrame(render);
		}

		final Player[] players = Players.getLoaded();
		for (final Player actor : players) {
			actor.draw(render, 0.08f);
		}

		final Npc[] npcs = Npcs.getLoaded();
		for (final Npc actor : npcs) {
			actor.draw(render, 0.08f);
		}

		final GroundItem[] groundItems = GroundItems.getLoaded(20);
		for (final GroundItem item : groundItems) {
			item.draw(render, 0.08f);
		}
	}
}