package org.powerbot.bot.rt6;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.Item;

public class DrawItems extends ClientAccessor implements PaintListener {

	public DrawItems(final ClientContext ctx) {
		super(ctx);
	}

	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}

		render.setFont(new Font("Arial", 0, 10));
		render.setColor(Color.green);

		if (ctx.bank.opened()) {
			final Component container = ctx.widgets.component(Constants.BANK_WIDGET, Constants.BANK_ITEMS);
			final Rectangle r = container.viewportRect();
			if (r != null) {
				for (final Item item : ctx.bank.select()) {
					final Component c = item.component();
					if (c == null) {
						continue;
					}
					final Rectangle r2 = c.boundingRect();
					if (r2 == null) {
						continue;
					}
					if (c.relativePoint().y == 0 || !r.contains(r2)) {
						continue;
					}
					final Point p = c.screenPoint();
					render.drawString(c.itemId() + "", p.x, p.y + c.height());
				}
			}
		}

		if (ctx.backpack.component().visible()) {
			for (final Item item : ctx.backpack.select()) {
				final Component c = item.component();
				if (c == null) {
					continue;
				}
				final Point p = c.screenPoint();
				render.drawString(c.itemId() + "", p.x, p.y + c.height());
			}
		}
		if (ctx.equipment.component().visible()) {
			for (final Item item : ctx.equipment.select()) {
				if (item == null) {
					continue;
				}
				final Component c = item.component();
				if (c == null) {
					continue;
				}
				final Point p = c.screenPoint();
				render.drawString(c.itemId() + "", p.x, p.y + c.height());
			}
		}
	}
}
