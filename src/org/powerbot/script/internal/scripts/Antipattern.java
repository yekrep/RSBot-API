package org.powerbot.script.internal.scripts;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Timer;

public class Antipattern extends PollingScript implements InternalScript {
	private final Timer timer;

	public Antipattern() {
		timer = new Timer(600 << 4);
		priority.set(1);
	}

	@Override
	public int poll() {
		if (timer.isRunning()) {
			threshold.set(0);
			return -1;
		}
		threshold.set(priority.get());

		timer.reset();
		ctx.antipatterns.run();
		return 0;
	}
}
