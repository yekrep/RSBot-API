package org.powerbot.game.bot.random;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class RandomHandler extends Task {
	private final Bot bot;
	private final AntiRandom[] antiRandoms;
	private final Map<String, EventListener> listeners = new HashMap<String, EventListener>();

	public RandomHandler(final Bot bot) {
		this.bot = bot;
		antiRandoms = new AntiRandom[]{
				new Login()
		};
	}

	public void run() {
		ActiveScript activeScript;
		while ((activeScript = bot.getActiveScript()) != null) {
			if (!activeScript.isRunning()) {
				Time.sleep(1000);
				continue;
			}
			Future<?> submittedRandom = null;
			for (final AntiRandom antiRandom : antiRandoms) {
				if (antiRandom.applicable()) {
					if (!activeScript.isPaused() || activeScript.getContainer().isActive()) {
						activeScript.pause(false);
						while (activeScript.getContainer().isActive()) {
							Time.sleep(Random.nextInt(500, 1200));
						}
					}
					activeScript.pause(true);
					bot.getEventDispatcher().accept(antiRandom);
					listeners.put(antiRandom.getClass().getName(), antiRandom);
					bot.getContainer().submit(antiRandom);
					if (antiRandom.future != null) {
						submittedRandom = antiRandom.future;
						break;
					}
				} else {
					final EventListener listener = listeners.remove(antiRandom.getClass().getName());
					if (listener != null) {
						bot.getEventDispatcher().remove(listener);
					}
				}
			}
			if (submittedRandom != null) {
				try {
					submittedRandom.get();
				} catch (final InterruptedException ignored) {
				} catch (final ExecutionException ignored) {
				}
			} else {
				if (activeScript.isPaused()) {
					activeScript.resume();
				}
				Time.sleep(Random.nextInt(1000, 5000));
			}
		}
	}
}
