package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Inventory;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class DrawInventory implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn() || Game.getCurrentTab() != Game.TAB_INVENTORY) {
			return;
		}
		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);
		Item[] items = Inventory.getItems();
		for (Item item : items) {
			Component c = item.getComponent();
			if (c == null) continue;
			Point p = c.getAbsoluteLocation();
			render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
		}
	}
}
