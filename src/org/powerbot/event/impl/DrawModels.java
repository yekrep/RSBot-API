package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.*;

import java.awt.*;

public class DrawModels implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};

	@Override
	public void onRepaint(final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GameObject[] objects = Filters.range(ctx.objects.getLoaded(), ctx.players.getLocal(), 10);
		for (final GameObject obj : objects) {
			if (!obj.isOnScreen()) continue;
			final Model m = obj.getModel();
			if (m == null) continue;
			final int o = obj.getType().ordinal();
			final int rgb = C[o].getRGB();
			render.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, A[o]));
			m.drawWireFrame(render);
		}

		Player[] players = ctx.players.getLoaded();
		for (final Player actor : players) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 10);
		}

		Npc[] npcs = ctx.npcs.getLoaded();
		for (final Npc actor : npcs) {
			if (!actor.isOnScreen()) continue;
			actor.draw(render, 20);
		}

		GroundItem[] groundItems = ctx.groundItems.getLoaded();
		groundItems = Filters.range(groundItems, ctx.players.getLocal(), 20);
		for (final GroundItem item : groundItems) {
			item.draw(render, 20);
		}
	}
}