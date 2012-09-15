package org.powerbot.core.randoms;

import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;

@Manifest(name = "Kiss the Frog", authors = {"Timer"}, version = 1.0)
public class Frog extends AntiRandom {
	private static final String NPC_NAME_HERALD = "Frog Herald";
	private static final Filter<NPC> NPC_FILTER_PRINCESS = new Filter<NPC>() {
		@Override
		public boolean accept(final NPC npc) {
			return !npc.isMoving() && npc.getHeight() == -278;
		}
	};

	@Override
	public boolean activate() {
		final NPC npc;
		return Game.isLoggedIn() && (npc = NPCs.getNearest(NPC_NAME_HERALD)) != null &&
				SceneEntities.getNearest(5917) != null && npc.getLocation().canReach();
	}

	@Override
	public void execute() {
		if (Widgets.clickContinue()) {
			sleep(Random.nextInt(2000, 3000));
			return;
		}

		if (Settings.get(334) == 0x1) {
			final NPC princess = NPCs.getNearest(NPC_FILTER_PRINCESS);
			if (princess != null) {
				if (princess.isOnScreen()) {
					if (princess.interact("Talk-to", "Frog")) {
						final Timer timer = new Timer(2000);
						while (timer.isRunning() && !Widgets.canContinue()) {
							sleep(150);
						}
					}
				} else {
					Walking.walk(princess.getLocation());
					final Timer timer = new Timer(2000);
					while (timer.isRunning()) {
						sleep(150);
						if (Players.getLocal().isMoving()) {
							timer.reset();
						}
					}
				}
			}
			return;
		}

		final NPC herald = NPCs.getNearest(NPC_NAME_HERALD);
		if (herald != null) {
			if (herald.isOnScreen()) {
				if (herald.interact("Talk-to", "Frog Herald")) {
					final Timer timer = new Timer(2000);
					while (timer.isRunning() && !Widgets.canContinue()) {
						sleep(150);
					}
				}
			} else {
				Walking.walk(herald.getLocation());
				final Timer timer = new Timer(2000);
				while (timer.isRunning()) {
					sleep(150);
					if (Players.getLocal().isMoving()) {
						timer.reset();
					}
				}
			}
		}
	}
}
