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

	private AntiRandom running;
	private Manifest manifest;
	private final Timer timeout;

	public RandomHandler(final Bot bot, final ScriptHandler scriptHandler) {
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
		for (final AntiRandom antiRandom : antiRandoms) {
			antiRandom.bot = bot;
		}
		handler = scriptHandler;
		running = null;
		timeout = new Timer(0);
	}

	@Override
	public int loop() {
		if (running != null && !running.activate()) {
			running = null;
			if (manifest != null) {
				log.info("Random solver completed: " + manifest.name());
			}

			handler.resume();
		}
		if (running == null) {
			for (final AntiRandom random : antiRandoms) {
				if (random.activate()) {
					running = random;
					timeout.setEndIn(Random.nextInt(240, 300) * 1000);

					final Class<?> clazz = running.getClass();
					manifest = clazz.isAnnotationPresent(Manifest.class) ? clazz.getAnnotation(Manifest.class) : null;
					if (manifest != null) {
						log.info("Random solver started: " + manifest.name());
					}

					handler.pause();
					break;
				}
			}
		}

		if (running != null) {
			if (!timeout.isRunning()) {
				handler.stop();

				if (manifest != null) {
					log.info("Random solver failed: " + manifest.name());
				}
				return -1;
			}

			running.execute();
			return 0;
		}

		return handler.isActive() ? 2000 : -1;
	}
}
