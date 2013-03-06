package org.powerbot.script.task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractContainer implements TaskContainer, TaskListener {
	private final ThreadGroup tg;
	private final ExecutorService executor;
	private final Set<Task> tasks;

	public AbstractContainer() {
		this.tg = new ThreadGroup(AbstractContainer.class.getName() + "@" + hashCode());
		this.executor = Executors.newCachedThreadPool(new Factory(this));
		this.tasks = new HashSet<>();
	}

	@Override
	public final boolean submit(final Task task) {
		if (executor.isShutdown()) return false;

		task.setContainer(this);
		task.setFuture(this.executor.submit(task));
		return true;
	}

	@Override
	public final void stop() {
		executor.shutdown();
	}

	@Override
	public final boolean isActive() {
		return !executor.isTerminated();
	}

	@Override
	public final boolean isStopped() {
		return executor.isShutdown();
	}

	@Override
	public void taskStarted(final Task task) {
		synchronized (this.tasks) {
			this.tasks.add(task);
		}
	}

	@Override
	public void taskStopped(final Task task) {
		synchronized (this.tasks) {
			this.tasks.remove(task);
		}
	}

	private final ThreadGroup getThreadGroup() {
		return this.tg;
	}

	private final class Factory implements ThreadFactory {
		private final String name;
		private final AbstractContainer container;
		private final AtomicInteger count;

		private Factory(final AbstractContainer container) {
			this.name = Factory.class.getName() + "@" + hashCode();
			this.container = container;
			this.count = new AtomicInteger(0);
		}

		@Override
		public Thread newThread(final Runnable r) {
			return new Thread(container.getThreadGroup(), r, name + count.incrementAndGet());
		}
	}
}
