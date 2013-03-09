package org.powerbot.script.internal.randoms;

import org.powerbot.script.TaskScript;
import org.powerbot.script.task.AsyncTask;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@RandomManifest(name = "Spin ticket destroyer")
public class TicketDestroy extends TaskScript {
	private static final int[] ITEM_IDS = {24154, 24155};

	public TicketDestroy() {
		submit(new Task());
	}

	private final class Task extends AsyncTask {
		private Item item;

		@Override
		public boolean isValid() {
			if (!Game.isLoggedIn() || Tabs.getCurrent() != Tabs.INVENTORY) return false;
			final Player player;
			if ((player = Players.getLocal()) == null ||
					player.isInCombat() || player.getAnimation() != -1 || player.getInteracting() != null) return false;
			item = Inventory.getItem(ITEM_IDS);
			return item != null;
		}

		@Override
		public void run() {
			final WidgetChild child = item.getWidgetChild();
			if (child != null) {
				if (((Settings.get(1448) & 0xFF00) >>> 8) < (child.getChildId() == ITEM_IDS[0] ? 10 : 9)) {
					child.interact("Claim spin");
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
		}
	}
}
