package org.powerbot.concurrent;

import java.util.concurrent.Future;

public abstract class ContainedTask implements Task {
	public Future<Object> future = null;

	public void setFuture(Future<Object> future) {
		this.future = future;
	}
}
