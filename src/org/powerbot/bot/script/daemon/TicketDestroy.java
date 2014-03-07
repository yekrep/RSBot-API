package org.powerbot.bot.script.daemon;

import java.util.concurrent.Callable;

import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.rs3.tools.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.rs3.tools.Component;
import org.powerbot.script.rs3.tools.Item;
import org.powerbot.script.rs3.tools.Widget;

/**
 */
public class TicketDestroy extends PollingScript implements InternalScript {
	private static final int[] ITEM_IDS = {24154, 24155};

	@Override
	public void poll() {
		final Item item = ctx.backpack.select().id(ITEM_IDS).poll();
		if (!item.isValid() || !ctx.hud.isVisible(Hud.Window.BACKPACK) || !ctx.players.local().isIdle()) {
			priority.set(0);
			return;
		}
		priority.set(3);
		if (!ctx.backpack.scroll(item)) {
			return;
		}

		if (((ctx.settings.get(1448) & 0xFF00) >>> 8) < (item.getId() == ITEM_IDS[0] ? 10 : 9)) {
			item.interact("Claim");
			return;
		}
		if (!item.interact("Destroy")) {
			return;
		}

		final Widget widget = ctx.widgets.get(1183);
		if (!Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return widget.isValid();
			}
		})) {
			return;
		}

		Component component = null;
		for (final Component c : widget.getComponents()) {
			if (c.isVisible() && c.getTooltip().trim().equalsIgnoreCase("destroy")) {
				component = c;
				break;
			}
		}
		if (component != null && component.interact("Destroy")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return item.getComponent().getItemId() == -1;
				}
			}, 175);
		}
		return;
	}
}
