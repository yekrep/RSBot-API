package org.powerbot.bot.rt6.activation;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Widget;

public class TicketDestroy extends PollingScript<ClientContext> {
	private static final int[] ITEM_IDS = {24154, 24155};

	@Override
	public void poll() {
		if (ctx.property("key.token.disable").equals("true")) {
			return;
		}
		final Item item = ctx.backpack.select().id(ITEM_IDS).poll();
		if (!item.valid() || !ctx.hud.opened(Hud.Window.BACKPACK) || !ctx.players.local().idle()) {
			priority.set(0);
			return;
		}
		priority.set(3);
		if (!ctx.backpack.scroll(item)) {
			return;
		}

		if (((ctx.varpbits.varpbit(1448) & 0xFF00) >>> 8) < (item.id() == ITEM_IDS[0] ? 10 : 9)) {
			item.interact("Claim");
			return;
		}
		if (!item.interact("Destroy")) {
			return;
		}

		final Widget widget = ctx.widgets.widget(1183);
		if (!Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
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
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return item.component().itemId() == -1;
				}
			}, 175);
		}
		return;
	}
}
