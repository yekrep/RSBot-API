package org.powerbot.script.framework;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.Script.State;

/**
 * A priority based {@code Script} controller.
 *
 * @author Paris
 */
public class ScriptManager implements ExecutorDispatch<Boolean>, Runnable, Stoppable, Suspendable {
	protected final ExecutorService executor;
	protected final Queue<ScriptDefinition> scripts;
	protected final AtomicBoolean suspended, stopping;
	private final ScriptController controller;
	private final EventManager events;
	protected final Queue<Runnable> callbacks;
	private final CallbackRunner runner;

	public ScriptManager(final EventMulticaster events) {
		executor = new NamedCachedThreadPoolExecutor();
		scripts = new PriorityQueue<>(4, new ScriptQueueComparator());
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);
		controller = new AbstractScriptController(this);
		this.events = new EventManager(events);
		callbacks = new ArrayDeque<>();
		runner = new CallbackRunner(executor, callbacks);
	}

	public ScriptManager(final EventMulticaster events, final Iterable<ScriptDefinition> scripts) {
		this(events);
		for (final ScriptDefinition script : scripts) {
			script.getScript().setScriptController(controller);
			this.events.add(script.getScript());
			this.scripts.add(script);
		}
	}

	public ExecutorService getExecutorService() {
		return executor;
	}

	public Queue<ScriptDefinition> getScripts() {
		return scripts;
	}

	public void addCallback(final Runnable e) {
		synchronized (callbacks) {
			callbacks.add(e);
		}
	}

	public Runnable removeCallback(final Runnable e) {
		synchronized (callbacks) {
			return callbacks.contains(e) && callbacks.remove(e) ? e : null;
		}
	}

	public ScriptController getScriptController() {
		return controller;
	}

	public EventManager getEventManager() {
		return events;
	}

	@Override
	public void run() {
		events.subscribeAll();
		call(State.START);
	}

	@Override
	public synchronized void stop() {
		stopping.set(true);
		events.unsubscribeAll();
		executor.execute(runner);
		while (!scripts.isEmpty()) {
			call(scripts.poll().getScript(), State.STOP);
		}
	}

	@Override
	public boolean isStopping() {
		return stopping.get();
	}

	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

	@Override
	public synchronized void suspend() {
		suspended.set(true);
		events.suspend();
		call(State.SUSPEND);
	}

	@Override
	public synchronized void resume() {
		call(State.RESUME);
		events.resume();
		suspended.set(false);
	}

	protected final void call(final State state) {
		executor.execute(runner);
		for (final ScriptDefinition script : scripts) {
			call(script.getScript(), state);
		}
	}

	protected final void call(final Script script, final State state) {
		final List<FutureTask<Boolean>> pending = new ArrayList<>();
		for (final FutureTask<Boolean> task : script.getTasks(state)) {
			if (task.isCancelled() || task.isDone()) {
				continue;
			}
			pending.add(task);
		}
		for (final FutureTask<Boolean> task : pending) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					while (!controller.getLockQueue().isEmpty() && !controller.getLockQueue().contains(script)) {
						try {
							Thread.currentThread().sleep(60);
						} catch (final InterruptedException ignored) {
						}
					}
					task.run();
				}
			});
		}
		for (final FutureTask<Boolean> task : pending) {
			boolean result = false;
			try {
				if (!task.isCancelled()) {
					result = task.get();
				}
			} catch (InterruptedException | ExecutionException ignored) {
			}
			if (!result) {
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit(final Runnable task) {
		executor.submit(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit(final Runnable task, final Boolean result) {
		executor.submit(task, result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit(final Callable<Boolean> task) {
		executor.submit(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submitSwing(final Runnable task) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(task);
			}
		});
	}

	private final class CallbackRunner implements Runnable {
		private final ExecutorService executor;
		private final Collection<Runnable> callbacks;

		public CallbackRunner(final ExecutorService executor, final Collection<Runnable> callbacks) {
			this.executor = executor;
			this.callbacks = callbacks;
		}

		@Override
		public void run() {
			synchronized (callbacks) {
				for (final Runnable e : callbacks) {
					executor.execute(e);
				}
			}
		}
	}

	private final class ScriptQueueComparator implements Comparator<ScriptDefinition> {

		@Override
		public int compare(final ScriptDefinition o1, final ScriptDefinition o2) {
			return o2.getScript().getPriority() - o1.getScript().getPriority();
		}
	}
}
