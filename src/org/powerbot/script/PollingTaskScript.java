package org.powerbot.script;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

import org.powerbot.script.task.AsyncTask;
import org.powerbot.script.task.BlockingTask;
import org.powerbot.script.task.Task;

/**
 * An implementation of {@code Script} which polls (or "loops") a set of tasks.
 *
 * @author Paris
 */
public abstract class PollingTaskScript extends PollingScript {
	private final Queue<Task> tasks;
	protected volatile int freq;

	public PollingTaskScript() {
		tasks = new PriorityQueue<Task>(4, new TaskQueueComparator());
		freq = 1000;
	}

	@Override
	public final int poll() {
		final ExecutorService executor = getScriptController().getExecutorService();
		final Stack<BlockingTask> sync = new Stack<BlockingTask>();
		for (final Task task : tasks) {
			if (task.isValid()) {
				if (task instanceof AsyncTask) {
					executor.submit((AsyncTask) task);
				} else if (task instanceof BlockingTask) {
					sync.add((BlockingTask) task);
				}
			}
		}
		executor.submit(new Runnable() {
			@Override
			public void run() {
				while (!sync.isEmpty()) {
					if (!sync.pop().call()) {
						break;
					}
				}
			}
		});
		return freq;
	}

	/**
	 * Retrieves the {@code Task} queue.
	 *
	 * @return the collection of {@code Task}s
	 */
	public Queue<Task> getTasks() {
		return tasks;
	}

	/**
	 * Adds a {@code Task} to the queue if it is not there already.
	 *
	 * @param task a {@code Task}
	 */
	public void submit(final Task task) {
		if (!tasks.contains(task)) {
			tasks.add(task);
		}
	}

	/**
	 * Removes a task from the queue if it exists.
	 *
	 * @param task a {@code Task}
	 */
	public void remove(final Task task) {
		if (tasks.contains(task)) {
			tasks.remove(task);
		}
	}

	public int getPollFrequency() {
		return freq;
	}

	public void setPollFrequency(final int freq) {
		this.freq = freq;
	}

	private final class TaskQueueComparator implements Comparator<Task> {

		@Override
		public int compare(final Task o1, final Task o2) {
			return o2.getPriority() - o1.getPriority();
		}
	}
}
