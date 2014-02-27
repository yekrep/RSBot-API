package org.powerbot.script.internal.environment;

import java.util.concurrent.Callable;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.rs3.tools.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.rs3.tools.Component;
import org.powerbot.script.rs3.tools.Item;
import org.powerbot.script.rs3.tools.Widget;

/**
 * @author Timer
 */
public class TicketDestroy extends PollingScript implements InternalScript {
	private static final int[] ITEM_IDS = {24154, 24155};

	public TicketDestroy() {
		priority.set(3);
	}

	@Override
	public int poll() {
		final Item item = ctx.backpack.select().id(ITEM_IDS).poll();
		if (!item.isValid() || !ctx.hud.isVisible(Hud.Window.BACKPACK) || !ctx.players.local().isIdle()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());
		if (!ctx.backpack.scroll(item)) {
			return -1;
		}

		if (((ctx.settings.get(1448) & 0xFF00) >>> 8) < (item.getId() == ITEM_IDS[0] ? 10 : 9)) {
			item.interact("Claim");
			return 1500;
		}
		if (!item.interact("Destroy")) {
			return Random.nextInt(1000, 2000);
		}

		final Widget widget = ctx.widgets.get(1183);
		if (!Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return widget.isValid();
			}
		})) {
			return -1;
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
		return -1;
	}
}
