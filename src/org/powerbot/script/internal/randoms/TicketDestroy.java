package org.powerbot.script.internal.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Widget;
import org.powerbot.util.Tracker;

@Manifest(name = "Spin ticket destroyer", authors = {"Timer"}, description = "Claims or destroys spin tickets")
public class TicketDestroy extends PollingPassive {
	private static final int[] ITEM_IDS = {24154, 24155};
	private Item item;

	public TicketDestroy(ClientFactory ctx, ScriptContainer container) {
		super(ctx, container);
	}

	@Override
	public int poll() {
		Tracker.getInstance().trackPage("randoms/TicketDestroy/", "");
		final Component child = item.getComponent();
		if (child == null || !item.isValid()) return -1;
		if (((ctx.settings.get(1448) & 0xFF00) >>> 8) < (child.getItemId() == ITEM_IDS[0] ? 10 : 9)) {
			child.interact("Claim spin");
			sleep(1000, 2000);
		}
		if (!child.interact("Destroy")) return Random.nextInt(1000, 2000);
		final Timer timer = new Timer(Random.nextInt(4000, 6000));
		while (timer.isRunning()) {
			final Widget widget = ctx.widgets.get(1183);
			if (widget != null && widget.isValid()) {
				for (final Component c : widget.getComponents()) {
					final String s;
					if (c.isVisible() && (s = c.getTooltip()) != null && s.trim().equalsIgnoreCase("destroy")) {
						if (c.interact("Destroy")) {
							final Timer t = new Timer(Random.nextInt(1500, 2000));
							while (t.isRunning() && child.getItemId() != -1) {
								sleep(100, 250);
							}
						}
					}
				}
			}
		}
		return -1;
	}

	@Override
	public boolean isValid() {
		if (!ctx.game.isLoggedIn() || ctx.game.getCurrentTab() != Game.TAB_INVENTORY) {
			return false;
		}
		final Player player;
		if ((player = ctx.players.getLocal()) == null ||
				player.isInCombat() || player.getAnimation() != -1 || player.getInteracting() != null) {
			return false;
		}
		item = ctx.inventory.select().id(ITEM_IDS).first();
		return item != null;
	}
}
