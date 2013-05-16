package org.powerbot.bot.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.client.input.Mouse;
import org.powerbot.event.PaintListener;
import org.powerbot.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class ViewMouse implements PaintListener, TextPaintListener {
	@Override
	public void onRepaint(final Graphics render) {
		final Client client = Bot.client();
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

	@Override
	public int draw(int idx, final Graphics render) {
		final Client client = Bot.client();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return idx;
		final Point loc = mouse.getLocation();
		StringUtil.drawLine(render, idx++, "Mouse location: " + loc.x + ", " + loc.y);
		return idx;
	}
}
