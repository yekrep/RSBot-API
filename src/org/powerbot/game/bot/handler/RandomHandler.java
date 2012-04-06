package org.powerbot.game.bot.handler;

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
import org.powerbot.game.api.randoms.Beekeeper;
import org.powerbot.game.api.randoms.Certer;
import org.powerbot.game.api.randoms.Chest;
import org.powerbot.game.api.randoms.DrillDemon;
import org.powerbot.game.api.randoms.FirstTimeDeath;
import org.powerbot.game.api.randoms.FreakyForester;
import org.powerbot.game.api.randoms.Frog;
import org.powerbot.game.api.randoms.GraveDigger;
import org.powerbot.game.api.randoms.Login;
import org.powerbot.game.api.randoms.LostAndFound;
import org.powerbot.game.api.randoms.Mime;
import org.powerbot.game.api.randoms.Pillory;
import org.powerbot.game.api.randoms.Pinball;
import org.powerbot.game.api.randoms.Quiz;
import org.powerbot.game.api.randoms.SandwichLady;
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
				new WidgetCloser(),
				new DrillDemon(),
				new Mime(),
				new Pinball(),
				new SandwichLady(),
				new Beekeeper(),
				new LostAndFound(),
				new FirstTimeDeath(),
				new Quiz(),
				new Frog(),
				new Chest(),
				new GraveDigger(),
				new Pillory(),
				new Certer(),
				new FreakyForester()
		};
		for (final AntiRandom antiRandom : antiRandoms) {
			antiRandom.bot = bot;
		}
	}

	public void run() {
		try {
			ActiveScript activeScript;
			while ((activeScript = bot.getActiveScript()) != null && activeScript.isRunning()) {
				if (bot.refreshing) {
					Time.sleep(500);
					continue;
				}
				Future<?> submittedRandom = null;
				for (final AntiRandom antiRandom : antiRandoms) {
					final boolean valid;
					try {
						valid = antiRandom.validate();
					} catch (final Throwable ignored) {
						continue;
					}
					if (valid) {
						if (!activeScript.isLocked() || activeScript.getContainer().isActive()) {
							log.info("Locking script");
							activeScript.silentLock(false);
							while (activeScript.getContainer().isActive()) {
								Time.sleep(Random.nextInt(500, 1200));
							}
						}
						if (!listeners.containsKey(antiRandom.getClass().getName())) {
							for (final EventListener listener : listeners.values()) {
								bot.getEventDispatcher().remove(listener);
							}
							listeners.clear();

							log.info("Activating random: " + antiRandom.getClass().getAnnotation(Manifest.class).name());
							activeScript.silentLock(true);
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

							if (activeScript.isLocked()) {
								log.info("Resuming active script processing");
								activeScript.resume();
							}
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
					Time.sleep(Random.nextInt(1000, 5000));
				}
			}
			log.info("Stopping");
			for (final EventListener listener : listeners.values()) {
				bot.getEventDispatcher().remove(listener);
			}
			listeners.clear();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
