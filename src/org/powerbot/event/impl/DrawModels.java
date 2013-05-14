package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

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
	private static final Map<GameObject.Type, Color> palette = new HashMap<>();

	static {
		palette.put(GameObject.Type.BOUNDARY, Color.black);
		palette.put(GameObject.Type.FLOOR_DECORATION, Color.yellow);
		palette.put(GameObject.Type.INTERACTIVE, Color.white);
		palette.put(GameObject.Type.WALL_DECORATION, Color.gray);
	}

	@Override
	public void onRepaint(final Graphics render) {
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Model m;
		final GameObject[] objects = Objects.getLoaded(20);
		for (final GameObject o : objects) {
			m = o.getModel();
			if (m == null) continue;
			final Color c = palette.get(o.getType());
			render.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 20));
			m.drawWireFrame(render);
		}

		render.setColor(new Color(255, 0, 0, 20));
		final Player[] players = Players.getLoaded();
		for (final Player actor : players) {
			m = actor.getModel();
			if (m == null) continue;
			m.drawWireFrame(render);
		}

		render.setColor(new Color(225, 0, 225, 20));
		final Npc[] npcs = Npcs.getLoaded();
		for (final Npc actor : npcs) {
			m = actor.getModel();
			if (m == null) continue;
			m.drawWireFrame(render);
		}

		render.setColor(new Color(225, 225, 0, 20));
		final GroundItem[] groundItems = GroundItems.getLoaded(20);
		for (final GroundItem item : groundItems) {
			m = item.getModel();
			if (m == null) continue;
			m.drawWireFrame(render);
		}
	}
}
