package org.powerbot.script.internal;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import org.powerbot.event.EventMulticaster;
import org.powerbot.script.ExecutorDispatch;
import org.powerbot.script.Script;
import org.powerbot.script.Script.State;
import org.powerbot.script.ScriptController;
import org.powerbot.script.Stoppable;
import org.powerbot.script.Suspendable;

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

	public final void call(final State state) {
		switch (state) {
		case START: run(); break;
		case STOP: stop(); break;
		case SUSPEND: suspend(); break;
		case RESUME: suspend(); break;
		}
	}

	protected final void call(final Script script, final State state) {
		for (final Future<Boolean> task : script.getTasks(state)) {
			if (task.isCancelled() || task.isDone()) {
				continue;
			}
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
