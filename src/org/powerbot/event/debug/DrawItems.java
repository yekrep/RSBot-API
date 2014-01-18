package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.event.PaintListener;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class DrawItems implements PaintListener {
	protected final MethodContext ctx;

	public DrawItems(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public void repaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);

		if (ctx.bank.isOpen()) {
			final Component container = ctx.widgets.get(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
			final Rectangle r = container.getViewportRect();
			if (r != null) {
				for (final Item item : ctx.bank.select()) {
					final Component c = item.getComponent();
					if (c == null) {
						continue;
					}
					final Rectangle r2 = c.getBoundingRect();
					if (r2 == null) {
						continue;
					}
					if (c.getRelativeLocation().y == 0 || !r.contains(r2)) {
						continue;
					}
					final Point p = c.getAbsoluteLocation();
					render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
				}
			}
		}

		if (ctx.backpack.getComponent().isVisible()) {
			for (final Item item : ctx.backpack.select()) {
				final Component c = item.getComponent();
				if (c == null) {
					continue;
				}
				final Point p = c.getAbsoluteLocation();
				render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
			}
		}
		if (ctx.equipment.getComponent().isVisible()) {
			for (final Item item : ctx.equipment.select()) {
				if (item == null) {
					continue;
				}
				final Component c = item.getComponent();
				if (c == null) {
					continue;
				}
				final Point p = c.getAbsoluteLocation();
				render.drawString(c.getItemId() + "", p.x, p.y + c.getHeight());
			}
		}
	}
}
