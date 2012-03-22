package org.powerbot.game.bot.handler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.random.AntiRandom;
import org.powerbot.game.bot.random.Login;

/**
 * @author Timer
 */
public class RandomHandler extends Task {
	private final Bot bot;
	private final AntiRandom[] antiRandoms;

	public RandomHandler(final Bot bot) {
		this.bot = bot;
		antiRandoms = new AntiRandom[]{
				new Login()
		};
	}

	public void run() {
		ActiveScript activeScript;
		while ((activeScript = bot.getActiveScript()) != null) {
			Future<?> submittedRandom = null;
			for (final AntiRandom antiRandom : antiRandoms) {
				if (antiRandom.applicable()) {
					activeScript.pause(true);
					while (activeScript.getContainer().isActive()) {
						Time.sleep(Random.nextInt(500, 1200));
					}

					bot.processor.submit(antiRandom);
					if (antiRandom.future != null) {
						submittedRandom = antiRandom.future;
						break;
					}
				}
			}
			if (submittedRandom != null) {
				try {
					submittedRandom.get();
				} catch (final InterruptedException ignored) {
				} catch (final ExecutionException ignored) {
				}
				activeScript.resume();
			} else {
				Time.sleep(Random.nextInt(1000, 5000));
			}
		}
	}
}
