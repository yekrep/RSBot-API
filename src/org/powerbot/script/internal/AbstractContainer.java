package org.powerbot.script.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.task.Task;
import org.powerbot.script.task.TaskContainer;
import org.powerbot.script.task.TaskListener;

public class AbstractContainer implements TaskContainer, TaskListener {
	protected final Set<Task> tasks;
	private final ThreadGroup tg;
	private final ExecutorService executor;

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
	public void stop() {
		executor.shutdown();
	}

	@Override
	public boolean isActive() {
		return !executor.isTerminated();
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	@Override
	public boolean isStopped() {
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

	protected final ThreadGroup getThreadGroup() {
		return this.tg;
	}

	protected final Task getTask(final Thread thread) {
		synchronized (this.tasks) {
			final Iterator<Task> iterator = this.tasks.iterator();
			while (iterator.hasNext()) {
				final Task task = iterator.next();
				if (task.getThread() == thread) return task;
			}
		}
		return null;
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
