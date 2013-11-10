package org.powerbot.script.internal.scripts;

import java.util.concurrent.Callable;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Widget;

/**
 * @author Timer
 */
public class TicketDestroy extends PollingScript implements InternalScript {
	private static final int[] ITEM_IDS = {24154, 24155};
	private Component component;

	public TicketDestroy() {
		priority.set(3);
	}

	private boolean isValid() {
		if (!ctx.game.isLoggedIn() || !ctx.hud.isVisible(Hud.Window.BACKPACK)) {
			return false;
		}

		final Player player;
		if ((player = ctx.players.local()) == null ||
				player.isInCombat() || player.getAnimation() != -1 || player.getInteracting() != null) {
			return false;
		}

		this.component = null;
		final Item item = ctx.backpack.select().id(ITEM_IDS).poll();
		if (item.isValid()) {
			this.component = item.getComponent();
			return this.component.isValid();
		}
		return false;
	}

	@Override
	public int poll() {
		if (!isValid()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());

		final Component item = this.component;
		if (item == null || !ctx.backpack.scroll(item)) {
			return -1;
		}

		if (((ctx.settings.get(1448) & 0xFF00) >>> 8) < (item.getItemId() == ITEM_IDS[0] ? 10 : 9)) {
			item.interact("Claim spin");
			sleep(1500);
			return -1;
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
		}, Random.nextInt(400, 600), 10)) {
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
					return item.getItemId() == -1;
				}
			}, Random.nextInt(150, 200), 10);
		}
		return -1;
	}
}
