package org.powerbot.script.task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TaskContainer implements TaskListener {
	private final ThreadGroup tg;
	private final ExecutorService executor;
	private final Set<Task> tasks;

	public TaskContainer() {
		this.tg = new ThreadGroup(TaskContainer.class.getName() + "@" + hashCode());
		this.executor = Executors.newCachedThreadPool(new Factory(this));
		this.tasks = new HashSet<>();
	}

	public final boolean submit(final Task task) {
		if (executor.isShutdown()) return false;

		task.setContainer(this);
		task.setFuture(this.executor.submit(task));
		return true;
	}

	public final boolean isActive() {
		synchronized (this.tasks) {
			return this.tasks.size() > 0;
		}
	}

	public final boolean isShutdown() {
		return executor.isShutdown();
	}

	public final void shutdown() {
		executor.shutdown();
	}

	public final void stop() {
		executor.shutdownNow();
	}

	@Override
	public final void taskStarted(final Task task) {
		synchronized (this.tasks) {
			this.tasks.add(task);
		}
	}

	@Override
	public final void taskStopped(final Task task) {
		synchronized (this.tasks) {
			this.tasks.remove(task);
		}
	}

	private ThreadGroup getThreadGroup() {
		return this.tg;
	}

	private final class Factory implements ThreadFactory {
		private final String name;
		private TaskContainer container;
		private int count = 0;

		private Factory(final TaskContainer container) {
			this.name = Factory.class.getName() + "@" + hashCode();
			this.container = container;
		}

		@Override
		public Thread newThread(final Runnable r) {
			return new Thread(container.getThreadGroup(), r, name + ++this.count);
		}
	}
}
