package org.powerbot.core.random;

import org.powerbot.core.random.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(name = "Squeal of Fortune Ticket Claim", authors = {"Timer"})
public class SpinTickets extends AntiRandom {
	public static final int ITEM_ID_SPIN_TICKET = 24154;
	public static final int ITEM_ID_SPIN_TICKET_X2 = 24155;

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Tabs.getCurrent() == Tabs.INVENTORY &&
				(Inventory.getItem(ITEM_ID_SPIN_TICKET) != null || Inventory.getItem(ITEM_ID_SPIN_TICKET_X2) != null) &&
				Players.getLocal().isIdle();
	}

	@Override
	public void run() {
		Item ticket = Inventory.getItem(ITEM_ID_SPIN_TICKET);
		if (ticket == null) {
			ticket = Inventory.getItem(ITEM_ID_SPIN_TICKET_X2);
		}

		if (ticket != null) {
			final WidgetChild item = ticket.getWidgetChild();
			if (item.interact("Destroy")) {
				final Timer timer = new Timer(Random.nextInt(2000, 3500));
				while (timer.isRunning() && !Widgets.get(1183).validate()) {
					Time.sleep(150);
				}
				final Widget chat = Widgets.get(1183);
				if (chat.validate()) {
					WidgetChild destroy = null;
					for (final WidgetChild child : chat.getChildren()) {
						final String toolTip;
						if ((toolTip = child.getTooltip()) != null && toolTip.trim().equalsIgnoreCase("Destroy") && child.visible()) {
							destroy = child;
							break;
						}
					}
					if (destroy == null) {
						return;
					}
					if (destroy.interact("Destroy")) {
						timer.reset();
						while (timer.isRunning() && item.validate()) {
							Time.sleep(Random.nextInt(100, 300));
						}
					}
				}
			}
		}
	}
}
