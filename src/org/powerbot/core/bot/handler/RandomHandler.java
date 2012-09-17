package org.powerbot.core.bot.handler;

import java.util.logging.Logger;

import org.powerbot.core.bot.Bot;
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
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;

/**
 * @author Timer
 */
public class RandomHandler extends LoopTask {
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());

	private final AntiRandom[] antiRandoms;
	private final ScriptHandler handler;

	private AntiRandom random;
	private Manifest manifest;
	private final Timer timeout;

	public RandomHandler(final ScriptHandler scriptHandler) {
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
				new FreakyForester(),
				new EvilTwin(),
				new EvilBob(),
				new Maze(),
				new Exam(),
				new ScapeRune(),
				new SpinTickets(),
				new BankPin()
		};

		handler = scriptHandler;
		random = null;
		timeout = new Timer(0);
	}

	@Override
	public int loop() {
		if (random != null) {
			if (!random.activate()) {
				process(false);
				return 0;
			}

			if (!timeout.isRunning()) {
				terminate();
				return -1;
			}

			random.execute();
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

	private AntiRandom next() {
		for (final AntiRandom random : antiRandoms) {
			if (random.activate()) {
				return random;
			}
		}
		return null;
	}

	private void process(final boolean start) {
		if (manifest != null) {
			log.info("Random solver " + (start ? "started" : "completed") + ": " + manifest.name());
		}

		if (start) {
			timeout.setEndIn(Random.nextInt(240, 300) * 1000);
			handler.pause();
		} else {
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
}
