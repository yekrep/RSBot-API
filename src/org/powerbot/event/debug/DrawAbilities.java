package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

public class DrawAbilities implements PaintListener {
	@Override
	public void repaint(Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);
		for (Action action : ctx.actionBar.getActions()) {
			if (action != null) {
				Component c = action.getComponent();
				Point p = c.getAbsoluteLocation();
				render.drawString(action.getId() + " (" + action.getBind() + ")", p.x, p.y);
			}
		}
	}
}
