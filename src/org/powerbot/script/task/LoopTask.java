package org.powerbot.script.task;

public abstract class LoopTask extends Task {
	public boolean onStart() {
		return true;
	}

	public void onFinish() {
	}

	public abstract int loop();

	@Override
	public void execute() {
		boolean running;
		try {
			running = onStart();
		} catch (final Throwable e) {
			e.printStackTrace();
			running = false;
		}
		if (!running) return;

		final TaskContainer container = getContainer();
		while (!container.isStopped()) {
			if (container.isPaused()) {
				sleep(500, 1000);
				continue;
			}

			int time;
			try {
				time = loop();
			} catch (final Throwable e) {
				e.printStackTrace();
				time = -1;
			}

			if (time >= 0) {
				sleep(time);
			} else if (time == -1) {
				break;
			}
		}

		onFinish();
	}
}
