package org.powerbot.bot.script.daemon;

import org.powerbot.script.PollingScript;
import org.powerbot.bot.script.InternalScript;
import org.powerbot.script.util.Timer;

@SuppressWarnings("deprecation")
public class Antipattern extends PollingScript implements InternalScript {
	private final Timer timer;

	public Antipattern() {
		timer = new Timer(600 << 4);
	}

	@Override
	public void poll() {
		if (timer.isRunning()) {
			priority.set(0);
			return;
		}
		priority.set(1);

		timer.reset();
		ctx.antipatterns.run();
	}
}
