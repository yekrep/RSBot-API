package org.powerbot.core.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class DrawModels implements PaintListener {
	private static final Map<Integer, Color> interactivePalette = new HashMap<>();

	static {
		interactivePalette.put(SceneEntities.TYPE_BOUNDARY, Color.black);
		interactivePalette.put(SceneEntities.TYPE_FLOOR_DECORATION, Color.yellow);
		interactivePalette.put(SceneEntities.TYPE_INTERACTIVE, Color.white);
		interactivePalette.put(SceneEntities.TYPE_WALL_DECORATION, Color.gray);
	}

	@Override
	public void onRepaint(final Graphics render) {
		/* Interactive entities */
		final SceneObject[] objects = SceneEntities.getLoaded();
		for (final SceneObject object : objects) {
			final CapturedModel model = object.getModel();
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
			final CapturedModel model = groundItem.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}

		/* Mobile entities */
		render.setColor(Color.red);
		final Player[] players = Players.getLoaded();
		for (final Player player : players) {
			final CapturedModel model = player.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}

		render.setColor(Color.magenta);
		final NPC[] npcs = NPCs.getLoaded();
		for (final NPC npc : npcs) {
			final CapturedModel model = npc.getModel();
			if (model == null || model.nextTriangle() == -1) continue;
			model.drawWireFrame(render);
		}
	}
}
