package org.powerbot.core.bot.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.powerbot.core.randoms.AntiRandom;
import org.powerbot.core.randoms.BankPin;
import org.powerbot.core.randoms.Beekeeper;
import org.powerbot.core.randoms.Certer;
import org.powerbot.core.randoms.Chest;
import org.powerbot.core.randoms.DrillDemon;
import org.powerbot.core.randoms.EvilBob;
import org.powerbot.core.randoms.EvilTwin;
import org.powerbot.core.randoms.Exam;
import org.powerbot.core.randoms.FirstTimeDeath;
import org.powerbot.core.randoms.FreakyForester;
import org.powerbot.core.randoms.Frog;
import org.powerbot.core.randoms.GraveDigger;
import org.powerbot.core.randoms.Login;
import org.powerbot.core.randoms.LostAndFound;
import org.powerbot.core.randoms.Maze;
import org.powerbot.core.randoms.Mime;
import org.powerbot.core.randoms.Pillory;
import org.powerbot.core.randoms.Pinball;
import org.powerbot.core.randoms.Quiz;
import org.powerbot.core.randoms.SandwichLady;
import org.powerbot.core.randoms.ScapeRune;
import org.powerbot.core.randoms.SpinTickets;
import org.powerbot.core.randoms.WidgetCloser;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;

/**
 * @author Timer
 */
public class RandomHandler extends LoopTask {
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());

	private final AntiRandom[] randoms;
	private final CopyOnWriteArrayList<AntiRandom> activeRandoms;
	private final ScriptHandler handler;

	private AntiRandom random;
	private Manifest manifest;
	private final Timer timeout;

	private EventListener[] listeners;

	public RandomHandler(final ScriptHandler scriptHandler) {
		randoms = new AntiRandom[]{
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
				new FreakyForester(),
				new EvilTwin(),
				new EvilBob(),
				new Maze(),
				new Exam(),
				new ScapeRune(),
				new SpinTickets(),
				new BankPin()
		};
		activeRandoms = new CopyOnWriteArrayList<>();
		activeRandoms.addAllAbsent(Arrays.asList(randoms));

		handler = scriptHandler;
		random = null;
		timeout = new Timer(0);

		listeners = null;
	}

	@Override
	public int loop() {
		if (random != null) {
			try {
				if (!random.activate()) {
					process(false);
					return 0;
				}
			} catch (final Exception ignored) {
				random = null;
				return 0;
			}

			if (!timeout.isRunning()) {
				terminate();
				return -1;
			}

			getContainer().submit(random);
			random.join();
			return 0;
		}

		if ((random = next()) != null) {
			final Class<?> clazz = random.getClass();
			manifest = clazz.isAnnotationPresent(Manifest.class) ? clazz.getAnnotation(Manifest.class) : null;
			process(true);
			return 0;
		}

		return handler.isActive() ? 2000 : -1;
	}

	public void enable(final Class<? extends AntiRandom> type, final boolean enable) {
		AntiRandom random = null;

		final String name = type.getName();
		for (final AntiRandom checkRandom : randoms) {
			if (checkRandom.getClass().getName().equals(name)) {
				random = checkRandom;
				break;
			}
		}

		if (random != null) {
			if (enable) {
				activeRandoms.addIfAbsent(random);
			} else {
				activeRandoms.remove(random);
			}
		}
	}

	public boolean isEnabled(final Class<? extends AntiRandom> type) {
		final String name = type.getName();
		for (final AntiRandom checkRandom : randoms) {
			if (checkRandom.getClass().getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	private AntiRandom next() {
		for (final AntiRandom random : activeRandoms) {
			try {
				if (random.activate()) {
					return random;
				}
			} catch (final Exception ignored) {
			}
		}
		return null;
	}

	private void process(final boolean start) {
		if (manifest != null) {
			log.info("Random solver " + (start ? "started" : "completed") + ": " + manifest.name());
		}

		if (start) {
			listeners = getJobListeners();
			for (final EventListener listener : listeners) {
				handler.eventManager.removeListener(listener);
			}
			handler.eventManager.addListener(random);

			if (random instanceof Login) {
				timeout.setEndIn(Random.nextInt(600, 720) * 1000);
			} else {
				timeout.setEndIn(Random.nextInt(300, 420) * 1000);
			}
			handler.pause();
		} else {
			if (listeners != null) {
				for (final EventListener listener : listeners) {
					handler.eventManager.addListener(listener);
				}
				listeners = null;
			}
			handler.eventManager.removeListener(random);

			random = null;
			manifest = null;
			handler.resume();
		}
	}

	private void terminate() {
		handler.stop();
		if (manifest != null) {
			log.warning("Random solver failed: " + manifest.name());
		}
	}

	private EventListener[] getJobListeners() {
		final EventListener[] listeners = handler.eventManager.getListeners();
		final List<EventListener> jobListenerList = new ArrayList<>(listeners.length);
		for (final EventListener listener : listeners) {
			if (listener instanceof Job) {
				jobListenerList.add(listener);
			}
		}
		return jobListenerList.toArray(new EventListener[jobListenerList.size()]);
	}
}
