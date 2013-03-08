package org.powerbot.script.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class BlockingTask extends FutureTask<Boolean> implements Task {

	public BlockingTask(final Runnable runnable, final boolean result) {
		super(runnable, result);
	}

	public BlockingTask(final Callable<Boolean> callable) {
		super(callable);
	}

	@Override
	public abstract boolean isValid();

	@Override
	public int getPriority() {
		return 0;
	}
}
