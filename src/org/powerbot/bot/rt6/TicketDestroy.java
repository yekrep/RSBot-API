package org.powerbot.bot.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Widget;

public class TicketDestroy extends PollingScript<ClientContext> {

	@Override
	public void poll() {
		if (ctx.properties.getProperty("key.token.disable", "").equals("true")) {
			return;//TODO: review this random event
		}
		final Item item = ctx.backpack.select().id(Constants.TICKETDESTROY_ITEMS).poll();
		if (!item.valid() || !ctx.hud.opened(Hud.Window.BACKPACK) || !ctx.players.local().idle()) {
			priority.set(0);
			return;
		}
		priority.set(3);
		if (!ctx.backpack.scroll(item)) {
			return;
		}

		if (((ctx.varpbits.varpbit(1448) & 0xFF00) >>> 8) < (item.id() == Constants.TICKETDESTROY_ITEMS[0] ? 10 : 9)) {
			item.interact("Claim");
			return;
		}
		if (!item.interact("Destroy")) {
			return;
		}

		final Widget widget = ctx.widgets.widget(1183);
		if (!Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return widget.valid();
			}
		})) {
			return;
		}

		Component component = null;
		for (final Component c : widget.components()) {
			if (c.visible() && c.tooltip().trim().equalsIgnoreCase("destroy")) {
				component = c;
				break;
			}
		}
		if (component != null && component.interact("Destroy")) {
			Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return item.component().itemId() == -1;
				}
			}, 175);
		}
	}
}
