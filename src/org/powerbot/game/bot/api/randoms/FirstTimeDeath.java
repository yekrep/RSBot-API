package org.powerbot.game.bot.api.randoms;

import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Location;

@Manifest(name = "First Time Death", authors = {"Timer"}, version = 1.1)
public class FirstTimeDeath extends AntiRandom {
	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Locations.getNearest(45802) != null;
	}

	@Override
	public void run() {
		if (Widgets.clickContinue()) {
			Time.sleep(Random.nextInt(800, 2000));
			return;
		}
		if (Widgets.get(1188, 3).validate() && (Widgets.get(1188, 3).getText().contains("How do i") || Widgets.get(1188, 3).getText().contains("bye"))) {
			Widgets.get(1188, 3).interact("Continue");
			return;
		}
		if (Widgets.get(1188, 24).validate()) {
			Widgets.get(1188, 24).interact("Continue");
			return;
		}

		final Location portal = Locations.getNearest(45803);
		if (portal.isOnScreen()) {
			portal.interact("Enter");

			final Timer timer = new Timer(5000);
			while (timer.isRunning()) {
				if (Players.getLocal().isMoving()) {
					timer.reset();
				}
			}
		} else {
			Camera.setPitch(true);
			Camera.turnTo(portal);
		}
	}
}
