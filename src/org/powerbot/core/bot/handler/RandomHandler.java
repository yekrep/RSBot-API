package org.powerbot.core.bot.handler;

import org.powerbot.core.random.AntiRandom;
import org.powerbot.core.random.BankPin;
import org.powerbot.core.random.Beekeeper;
import org.powerbot.core.random.Certer;
import org.powerbot.core.random.Chest;
import org.powerbot.core.random.DrillDemon;
import org.powerbot.core.random.EvilBob;
import org.powerbot.core.random.EvilTwin;
import org.powerbot.core.random.Exam;
import org.powerbot.core.random.FirstTimeDeath;
import org.powerbot.core.random.FreakyForester;
import org.powerbot.core.random.Frog;
import org.powerbot.core.random.GraveDigger;
import org.powerbot.core.random.Login;
import org.powerbot.core.random.LostAndFound;
import org.powerbot.core.random.Maze;
import org.powerbot.core.random.Mime;
import org.powerbot.core.random.Pillory;
import org.powerbot.core.random.Pinball;
import org.powerbot.core.random.Quiz;
import org.powerbot.core.random.SandwichLady;
import org.powerbot.core.random.ScapeRune;
import org.powerbot.core.random.SpinTickets;
import org.powerbot.core.random.WidgetCloser;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.bot.Bot;

public class RandomHandler extends LoopTask {
	private final AntiRandom[] antiRandoms;
	private final ScriptHandler handler;
	private AntiRandom running;

	public RandomHandler(final Bot bot) {
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
		handler = bot.getScriptHandler();
		running = null;
	}

	@Override
	public int loop() {
		if (running != null && !running.validate()) {
			running = null;
		}
		if (running == null) {
			for (final AntiRandom random : antiRandoms) {
				if (random.validate()) {
					running = random;
					break;
				}
			}
		}

		if (running != null) {
			running.run();
			return 0;
		}

		return handler.isActive() ? 2000 : -1;
	}
}
