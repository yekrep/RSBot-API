package org.powerbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.event.PaintListener;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.tabs.Inventory;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Item;

public class DrawInventory implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn() || Game.getCurrentTab() != Game.TAB_INVENTORY) {
			return;
		}
		render.setColor(Color.green);
		final FontMetrics fontMetrics = render.getFontMetrics();
		final Item[] items = Inventory.getItems();
		if (items != null) {
			for (final Item item : items) {
				if (item != null) {
					final Component child = item.getComponent();
					if (child != null && child.isValid() && child.getItemId() != -1) {
						final Point center = child.getAbsoluteLocation();
						final String id = item.getId() + "";
						render.drawString(id, center.x, center.y + fontMetrics.getHeight());
					}
				}
			}
		}
	}
}
