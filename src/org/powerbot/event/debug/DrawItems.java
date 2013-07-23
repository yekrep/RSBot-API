package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class DrawItems implements PaintListener {
	public void repaint(final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);

		if (ctx.bank.isOpen()) {
			Component container = ctx.widgets.get(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
			Rectangle r = container.getViewportRect();
			if (r != null) {
				for (Item item : ctx.bank.select()) {
					Component c = item.getComponent();
					if (c == null) {
						continue;
					}
					Rectangle r2 = c.getBoundingRect();
					if (r2 == null) {
						continue;
					}
					if (c.getRelativeLocation().y == 0 || !r.contains(r2)) {
						continue;
					}
					Point p = c.getAbsoluteLocation();
					render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
				}
			}
		}

		if (ctx.backpack.getComponent().isVisible()) {
			for (Item item : ctx.backpack.select()) {
				Component c = item.getComponent();
				if (c == null) {
					continue;
				}
				Point p = c.getAbsoluteLocation();
				render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
			}
		}
		if (ctx.equipment.getComponent().isVisible()) {
			for (Item item : ctx.equipment.getAllItems()) {
				if (item == null) {
					continue;
				}
				Component c = item.getComponent();
				if (c == null) {
					continue;
				}
				Point p = c.getAbsoluteLocation();
				render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
			}
		}
	}
}
