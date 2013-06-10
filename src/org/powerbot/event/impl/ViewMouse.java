package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.bot.ClientFactory;
import org.powerbot.client.Client;
import org.powerbot.client.input.Mouse;
import org.powerbot.event.PaintListener;

public class ViewMouse implements PaintListener {
	@Override
	public void onRepaint(final Graphics render) {
		final Client client = ClientFactory.getFactory().getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return;

		Point loc = mouse.getLocation();
		render.setColor(Color.yellow.darker());
		render.drawLine(loc.x - 5, loc.y - 5, loc.x + 5, loc.y + 5);
		render.drawLine(loc.x + 5, loc.y - 5, loc.x - 5, loc.y + 5);

		if (System.currentTimeMillis() - mouse.getPressTime() < 1000) {
			loc = mouse.getPressLocation();
			render.setColor(Color.red.brighter());
			render.drawLine(loc.x - 5, loc.y - 5, loc.x + 5, loc.y + 5);
			render.drawLine(loc.x + 5, loc.y - 5, loc.x - 5, loc.y + 5);
		}
	}
}
