package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.tabs.Inventory;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class DrawInventory implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn() || Game.getCurrentTab() != Game.TAB_INVENTORY) {
			return;
		}
		render.setColor(Color.green);
		final FontMetrics fontMetrics = render.getFontMetrics();
		final Item[] items = Inventory.getItems();
		for (final Item item : items) {
			final Component c = item.getComponent();
			final Point p = c.getAbsoluteLocation();
			render.drawString(c.getItemId() + "", p.x, p.y + fontMetrics.getHeight());
		}
	}
}
