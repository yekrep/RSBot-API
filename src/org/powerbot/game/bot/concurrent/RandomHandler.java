package org.powerbot.game.bot.concurrent;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.randoms.Login;
import org.powerbot.game.api.randoms.WidgetCloser;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class RandomHandler implements Task {
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());

	private final Bot bot;
	private final AntiRandom[] antiRandoms;
	private final Map<String, EventListener> listeners = new HashMap<String, EventListener>();

	public RandomHandler(final Bot bot) {
		this.bot = bot;
		antiRandoms = new AntiRandom[]{
				new Login(),
				new WidgetCloser()
		};
		for (final AntiRandom antiRandom : antiRandoms) {
			antiRandom.bot = bot;
		}
	}

	public void run() {
		ActiveScript activeScript;
		while ((activeScript = bot.getActiveScript()) != null && activeScript.isRunning()) {
			Future<?> submittedRandom = null;
			for (final AntiRandom antiRandom : antiRandoms) {
				if (antiRandom.applicable()) {
					if (!activeScript.isPaused() || activeScript.getContainer().isActive()) {
						log.info("Locking script");
						activeScript.pause(false);
						while (activeScript.getContainer().isActive()) {
							Time.sleep(Random.nextInt(500, 1200));
						}
					}
					if (!listeners.containsKey(antiRandom.getClass().getName())) {
						log.info("Activating random: " + antiRandom.getClass().getAnnotation(Manifest.class).name());
						activeScript.pause(true);
						bot.getEventDispatcher().accept(antiRandom);
						listeners.put(antiRandom.getClass().getName(), antiRandom);
					}
					submittedRandom = bot.getContainer().submit(antiRandom);
					if (submittedRandom != null) {
						break;
					}
				} else {
					final EventListener listener = listeners.remove(antiRandom.getClass().getName());
					if (listener != null) {
						log.info("Deactivating random: " + antiRandom.getClass().getAnnotation(Manifest.class).name());
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
					log.info("Resuming active script processing");
					activeScript.resume();
				}
				Time.sleep(Random.nextInt(1000, 5000));
			}
		}
	}
}
