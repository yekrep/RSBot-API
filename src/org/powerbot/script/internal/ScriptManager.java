package org.powerbot.script.internal;

import java.util.ArrayList;
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
import org.powerbot.script.util.AbstractScriptController;
import org.powerbot.script.util.EventManager;
import org.powerbot.script.util.NamedCachedThreadPoolExecutor;
import org.powerbot.script.util.ScriptController;
import org.powerbot.script.util.Stoppable;
import org.powerbot.script.util.Suspendable;
import org.powerbot.script.xenon.util.ExecutorDispatch;

/**
 * A priority based {@code Script} controller.
 *
 * @author Paris
 */
public class ScriptManager implements ExecutorDispatch<Boolean>, Runnable, Stoppable, Suspendable {
	protected final ExecutorService executor;
	protected final Queue<Script> scripts;
	protected final AtomicBoolean suspended, stopping;
	private final ScriptController controller;
	private final EventManager events;

	public ScriptManager(final EventMulticaster events) {
		executor = new NamedCachedThreadPoolExecutor();
		scripts = new PriorityQueue<Script>(4, new ScriptQueueComparator());
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);
		controller = new AbstractScriptController(this);
		this.events = new EventManager(events);
	}

	public ScriptManager(final EventMulticaster events, final Script... scripts) {
		this(events);
		for (final Script script : scripts) {
			script.setScriptController(controller);
			this.scripts.add(script);
		}
		run();
	}

	public ExecutorService getExecutorService() {
		return executor;
	}

	public Queue<Script> getScripts() {
		return scripts;
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
		while (!scripts.isEmpty()) {
			call(scripts.poll(), State.STOP);
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
		for (final Script script : scripts) {
			call(script, state);
		}
	}

	protected final void call(final Script script, final State state) {
		final List<FutureTask<Boolean>> pending = new ArrayList<FutureTask<Boolean>>();
		for (final FutureTask<Boolean> task : script.getTasks(state)) {
			if (task.isCancelled() || task.isDone()) {
				continue;
			}
			pending.add(task);
		}
		for (final FutureTask<Boolean> task : pending) {
			executor.execute(task);
		}
		for (final FutureTask<Boolean> task : pending) {
			boolean result = false;
			try {
				result = task.get();
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

	private final class ScriptQueueComparator implements Comparator<Script> {

		@Override
		public int compare(final Script o1, final Script o2) {
			return o2.getPriority() - o1.getPriority();
		}
	}
}
