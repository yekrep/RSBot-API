package org.powerbot.script;

import java.util.concurrent.Callable;

/**
 * An event dispatcher.
 *
 * @author Paris
 *
 * @param <T>
 */
public interface ExecutorDispatch<T> {

	public void submit(Runnable task);

	public void submit(Runnable task, T result);

	public void submit(Callable<T> task);

	/**
	 * Submit a task to run on the Swing event queue.
	 *
	 * @param task a {@code Runnable} task
	 */
	public void submitSwing(Runnable task);
}
