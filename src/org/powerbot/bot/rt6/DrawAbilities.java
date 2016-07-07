package org.powerbot.bot.rt6;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Action;
import org.powerbot.script.rt6.Component;

public class DrawAbilities extends ClientAccessor implements PaintListener {

	public DrawAbilities(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}
		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);
		for (final Action action : ctx.combatBar.actions()) {
			final Component c = action.component();
			final Point p = c.screenPoint();
			render.drawString(action.id() + " (" + action.bind() + ")", p.x, p.y);
		}
	}
}
