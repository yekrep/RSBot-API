package org.powerbot.script.internal;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
public class ScriptManager implements Runnable, Stoppable, Suspendable {
	protected final ExecutorService executor;
	protected final Queue<Script> scripts;
	protected final AtomicBoolean suspended, stopping;
	private final ScriptController controller;

	public ScriptManager() {
		executor = new NamedCachedThreadPoolExecutor();
		scripts = new PriorityQueue<Script>(4, new ScriptQueueComparator());
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);
		controller = new AbstractScriptController(this);
	}

	public ScriptManager(final Script... scripts) {
		this();
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

	@Override
	public void run() {
		call(State.START);
	}

	@Override
	public synchronized void stop() {
		stopping.set(true);
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
		call(State.SUSPEND);
	}

	@Override
	public synchronized void resume() {
		call(State.RESUME);
		suspended.set(false);
	}

	public final void call(final State state) {
		for (final Script script : scripts) {
			call(script, state);
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

	private final class ScriptQueueComparator implements Comparator<Script> {

		@Override
		public int compare(final Script o1, final Script o2) {
			return o2.getPriority() - o1.getPriority();
		}
	}

	private final class AbstractScriptController implements ScriptController {
		private final ScriptManager manager;

		public AbstractScriptController(final ScriptManager manager) {
			this.manager = manager;
		}

		@Override
		public boolean isStopping() {
			return manager.isStopping();
		}

		@Override
		public void stop() {
			manager.stop();
		}

		@Override
		public boolean isSuspended() {
			return manager.isSuspended();
		}

		@Override
		public void suspend() {
			manager.suspend();
		}

		@Override
		public void resume() {
		}

		@Override
		public ExecutorService getExecutorService() {
			return manager.getExecutorService();
		}
	}
}
