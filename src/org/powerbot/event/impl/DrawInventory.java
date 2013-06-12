package org.powerbot.event.impl;

import org.powerbot.bot.Bot;
import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.Game;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.awt.*;

public class DrawInventory implements PaintListener {
	public void onRepaint(final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		if (!ctx.game.isLoggedIn() || ctx.game.getCurrentTab() != Game.TAB_INVENTORY) {
			return;
		}
		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);
		Item[] items = ctx.inventory.getItems();
		for (Item item : items) {
			Component c = item.getComponent();
			if (c == null) continue;
			Point p = c.getAbsoluteLocation();
			render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
		}
	}
}
