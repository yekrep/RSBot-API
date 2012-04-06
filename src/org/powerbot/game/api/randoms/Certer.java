package org.powerbot.game.api.randoms;

import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Npcs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Mobile;
import org.powerbot.game.api.wrappers.interactive.Npc;
import org.powerbot.game.api.wrappers.node.Location;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(name = "Certer", authors = {"Timer"}, version = 1.0)
public class Certer extends AntiRandom {
	private static final int LOCATION_ID_PORTAL = 11368;
	private static final int SETTING_SOLVED = 807;
	private static final int WIDGET_IDENTIFY = 184;
	private static final int WIDGET_IDENTIFY_ITEM = 8;
	private static final int WIDGET_IDENTIFY_ITEM_MODEL = 3;
	private static final int[] WIDGET_MODEL_IDS = {2807, 8828, 8829, 8832, 8833, 8834, 8835, 8836, 8837};
	private static final int[] LOCATION_ID_BOOKS = {42352, 42354};
	private static final String[] ITEM_NAMES = {"bowl", "battleaxe", "fish",
			"shield", "helmet", "ring", "shears", "sword", "spade"};

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Locations.getNearest(LOCATION_ID_BOOKS) != null;
	}

	@Override
	public void run() {
		if (Players.getLocal().getAnimation() != -1 || Players.getLocal().isMoving()) {
			Time.sleep(Random.nextInt(500, 1000));
			return;
		}
		if (Settings.get(SETTING_SOLVED) == 0x1982899a) {
			final Location portal = Locations.getNearest(LOCATION_ID_PORTAL);
			if (portal != null) {
				if (!portal.isOnScreen()) {
					walk(portal);
					return;
				}
				if (portal.interact("Enter", "Exit")) {
					Time.sleep(Random.nextInt(3000, 5000));
				}
			}
			return;
		}
		if (Widgets.clickContinue()) {
			Time.sleep(Random.nextInt(500, 1000));
			return;
		}
		if (Widgets.get(WIDGET_IDENTIFY, WIDGET_IDENTIFY_ITEM).validate()) {
			final int model = Widgets.get(WIDGET_IDENTIFY, WIDGET_IDENTIFY_ITEM).getChild(WIDGET_IDENTIFY_ITEM_MODEL).getModelId();
			String itemName = null;
			for (int i = 0; i < WIDGET_MODEL_IDS.length; i++) {
				if (WIDGET_MODEL_IDS[i] == model) {
					itemName = ITEM_NAMES[i];
				}
			}
			if (itemName != null) {
				for (int j = 0; j < 3; j++) {
					final WidgetChild child = Widgets.get(WIDGET_IDENTIFY, WIDGET_IDENTIFY_ITEM).getChild(j);
					if (child.getText().toLowerCase().contains(itemName.toLowerCase())) {
						if (child.click(true)) {
							Time.sleep(Random.nextInt(1000, 1200));
						}
					}
				}
			}
			return;
		}

		final Npc man = Npcs.getNearest(new Filter<Npc>() {
			@Override
			public boolean accept(final Npc npc) {
				final String name = npc.getName().toLowerCase();
				return name.equals("giles") || name.equals("miles") || name.equals("niles");
			}
		});

		if (man != null) {
			if (!man.isOnScreen()) {
				walk(man);
				return;
			}
			if (man.interact("Talk-to")) {
				final Timer timer = new Timer(2000);
				while (timer.isRunning() && !Widgets.canContinue()) {
					Time.sleep(150);
					if (Players.getLocal().isMoving()) {
						timer.reset();
					}
				}
			}
		}
	}

	private void walk(final Mobile mobile) {
		Walking.walk(mobile);
		final Timer timer = new Timer(2000);
		while (timer.isRunning()) {
			if (mobile.getPosition().isOnScreen()) {
				break;
			}
			if (Players.getLocal().isMoving()) {
				timer.reset();
			}
			Time.sleep(150);
		}
	}
}
