package org.powerbot.script.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.script.Script;
import org.powerbot.script.Script.State;
import org.powerbot.script.Suspendable;

/**
 * A priority based {@code Script} controller.
 *
 * @author Paris
 */
public class ScriptController implements Runnable, Suspendable {
	protected final ExecutorService executor;
	protected final Queue<Script> scripts;
	protected final AtomicBoolean suspended, closing;

	public ScriptController() {
		executor = new NamedCachedThreadPoolExecutor();
		scripts = new PriorityQueue<Script>(4, new ScriptQueueComparator());
		suspended = new AtomicBoolean(false);
		closing = new AtomicBoolean(false);
	}

	public ScriptController(final Script... scripts) {
		this();
		this.scripts.addAll(Arrays.asList(scripts));
	}

	public Queue<Script> getScripts() {
		return scripts;
	}

	@Override
	public void run() {
		call(State.START);
	}

	public void close() {
		closing.set(true);
		while (!scripts.isEmpty()) {
			call(scripts.poll(), State.STOP);
		}
	}

	public boolean isClosing() {
		return closing.get();
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
}
