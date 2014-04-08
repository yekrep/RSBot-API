package org.powerbot.bot.rt6.activation;

import java.awt.Color;
import java.awt.Graphics;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.GroundItem;
import org.powerbot.script.rt6.Model;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.Player;

public class DrawModels extends ClientAccessor implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};
	private static final int[] A = {25, 40, 255, 50};

	public DrawModels(final ClientContext ctx) {
		super(ctx);
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