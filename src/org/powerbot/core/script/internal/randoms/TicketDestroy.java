package org.powerbot.core.script.internal.randoms;

import org.powerbot.core.script.methods.Game;
import org.powerbot.core.script.methods.Players;
import org.powerbot.core.script.util.Random;
import org.powerbot.core.script.util.Timer;
import org.powerbot.core.script.wrappers.Player;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@RandomManifest(name = "Spin ticket destroyer")
public class TicketDestroy extends AntiRandom {
	private static final int[] ITEM_IDS = {24154, 24155};
	private Item item;

	@Override
	public boolean valid() {
		if (!Game.isLoggedIn() || Tabs.getCurrent() != Tabs.INVENTORY) return false;
		final Player player;
		if ((player = Players.getLocal()) == null ||
				player.isInCombat() || player.getAnimation() != -1 || player.getInteracting() != null) return false;
		item = Inventory.getItem(ITEM_IDS);
		return item != null;
	}

	@Override
	public int loop() {
		if (!valid()) return -1;

		final WidgetChild child = item.getWidgetChild();
		if (child != null) {
			if (((Settings.get(1448) & 0xFF00) >>> 8) < 10) {
				child.interact("Claim spin");
				return Random.nextInt(1000, 2000);
			}
			if (child.interact("Destroy")) {
				final Timer timer = new Timer(Random.nextInt(4000, 6000));
				while (timer.isRunning()) {
					final Widget widget = Widgets.get(1183);
					if (widget != null && widget.validate()) {
						for (final WidgetChild c : widget.getChildren()) {
							final String s;
							if (c.visible() && (s = c.getTooltip()) != null && s.trim().equalsIgnoreCase("destroy")) {
								if (c.interact("Destroy")) {
									final Timer t = new Timer(Random.nextInt(1500, 2000));
									while (t.isRunning() && child.getChildId() != -1) sleep(100, 250);
								}
							}
						}
					}
				}
			}
		}
		return Random.nextInt(200, 700);
	}
}
