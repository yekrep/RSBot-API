package org.powerbot.bot.rs3.event.debug;

import java.awt.Color;
import java.awt.Graphics;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rs3.ClientContext;
import org.powerbot.script.rs3.GameObject;
import org.powerbot.script.rs3.GroundItem;
import org.powerbot.script.rs3.Model;
import org.powerbot.script.rs3.Npc;
import org.powerbot.script.rs3.Player;

public class DrawModels implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};
	private final ClientContext ctx;

	public DrawModels(final ClientContext ctx) {
		this.ctx = ctx;
	}


	@Override
	public void repaint(final Graphics render) {
		for (final GameObject obj : ctx.objects.select().within(10)) {
			if (!obj.inViewport()) {
				continue;
			}
			final Model m = obj.model();
			if (m == null) {
				continue;
			}
			final int o = obj.type().ordinal();
			final int rgb = C[o].getRGB();
			render.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, A[o]));
			m.drawWireFrame(render);
		}

		for (final Player actor : ctx.players.select()) {
			if (!actor.inViewport()) {
				continue;
			}
			actor.draw(render, 10);
		}

		for (final Npc actor : ctx.npcs.select()) {
			if (!actor.inViewport()) {
				continue;
			}
			actor.draw(render, 20);
		}

		for (final GroundItem item : ctx.groundItems.select().within(20)) {
			item.draw(render, 20);
		}
	}
}