package org.powerbot.script.internal.scripts;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.util.Timer;

public class Antipattern extends PollingScript implements InternalScript {
	private final Timer timer;

	public Antipattern() {
		timer = new Timer(600 << 4);
	}

	@Override
	public boolean isValid() {
		return !timer.isRunning();
	}

	@Override
	public int poll() {
		if (!isValid()) {
			return -1;
		}

		timer.reset();
		ctx.antipatterns.run();
		return 0;
	}

	@Override
	public int getPriority() {
		return Thread.NORM_PRIORITY;
	}
}
