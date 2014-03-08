package org.powerbot.bot.rs3.event.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rs3.tools.ClientContext;
import org.powerbot.script.rs3.tools.Action;
import org.powerbot.script.rs3.tools.Component;

public class DrawAbilities implements PaintListener {
	private final ClientContext ctx;

	public DrawAbilities(final ClientContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);
		for (final Action action : ctx.combatBar.getActions()) {
			final Component c = action.getComponent();
			final Point p = c.getAbsoluteLocation();
			render.drawString(action.getId() + " (" + action.getBind() + ")", p.x, p.y);
		}
	}
}
