package org.powerbot.script.task;

import java.util.concurrent.Callable;

public abstract class BlockingTask implements Task, Callable<Boolean> {

	@Override
	public abstract boolean isValid();

	@Override
	public abstract Boolean call();

	@Override
	public int getPriority() {
		return 0;
	}
}
