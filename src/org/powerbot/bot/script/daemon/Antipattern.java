package org.powerbot.bot.script.daemon;

import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.util.Timer;

@SuppressWarnings("deprecation")
public class Antipattern extends PollingScript implements InternalScript {
	private final Timer timer;

	public Antipattern() {
		timer = new Timer(600 << 4);
		priority.set(1);
	}

	@Override
	public int poll() {
		if (timer.isRunning()) {
			threshold.poll();
			return 0;
		}
		threshold.offer(priority.get());

		timer.reset();
		ctx.antipatterns.run();
		return 0;
	}
}
