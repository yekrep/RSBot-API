package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
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
	private static final Map<GameObject.Type, Color> interactivePalette = new HashMap<>();

	static {
		interactivePalette.put(GameObject.Type.BOUNDARY, Color.black);
		interactivePalette.put(GameObject.Type.FLOOR_DECORATION, Color.yellow);
		interactivePalette.put(GameObject.Type.INTERACTIVE, Color.white);
		interactivePalette.put(GameObject.Type.WALL_DECORATION, Color.gray);
	}

	@Override
	public void onRepaint(final Graphics render) {
		/* Interactive entities */
		final GameObject[] objects = Objects.getLoaded();
		for (final GameObject object : objects) {
			final Model model = object.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			render.setColor(interactivePalette.get(object.getType()));
			model.drawWireFrame(render);

			final Point p = model.getCenterPoint();
			render.setColor(Color.green);
			render.fillOval(p.x - 2, p.y - 2, 5, 5);
		}

		/* Renderable entities */
		render.setColor(Color.blue);
		final GroundItem[] groundItems = GroundItems.getLoaded();
		for (final GroundItem groundItem : groundItems) {
			final Model model = groundItem.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}

		/* Mobile entities */
		render.setColor(Color.red);
		final Player[] players = Players.getLoaded();
		for (final Player player : players) {
			final Model model = player.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}

		render.setColor(Color.magenta);
		final Npc[] npcs = Npcs.getLoaded();
		for (final Npc npc : npcs) {
			final Model model = npc.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}
	}
}
