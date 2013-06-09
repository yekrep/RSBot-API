package org.powerbot.nscript.internal;

import org.powerbot.nscript.Script;
import org.powerbot.nscript.lang.Stoppable;
import org.powerbot.nscript.lang.Suspendable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptHandler implements Suspendable, Stoppable {
	private ExecutorService executor;
	private AtomicReference<Script> script;
	private AtomicBoolean suspended;
	private AtomicBoolean stopping;

	public ScriptHandler() {
		this.executor = new ScriptExecutor(this);
		this.script = new AtomicReference<>(null);
		this.suspended = new AtomicBoolean(false);
		this.stopping = new AtomicBoolean(false);
	}

	public boolean start(Script script) {
		if (this.script.compareAndSet(null, script)) {
			script.getTriggers(Script.Event.START).offerLast(new FutureTask<>(script, true));
			if (call(Script.Event.START)) return true;
			else this.script.set(null);
		}
		return false;
	}

	@Override
	public boolean isStopping() {
		return stopping.get();
	}

	@Override
	public void stop() {
		if (!stopping.compareAndSet(false, true)) return;
		getExecutor().shutdown();
		//TODO ensure everything gets shutdown
	}

	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

	@Override
	public void setSuspended(boolean suspended) {
		if (this.suspended.compareAndSet(!suspended, suspended)) {
			if (!call(suspended ? Script.Event.SUSPEND : Script.Event.RESUME)) {
				this.suspended.compareAndSet(suspended, !suspended);
			}
		}
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	private boolean call(Script.Event event) {
		Script script = this.script.get();
		if (script == null) return false;

		Collection<FutureTask<Boolean>> tasks = script.getTriggers(event);
		List<FutureTask<Boolean>> pending = new ArrayList<>(tasks.size());
		pending.addAll(tasks);

		Executor service = getExecutor();
		for (FutureTask<Boolean> task : pending) {
			service.execute(task);
			boolean result = false;
			try {
				result = task.get();
			} catch (InterruptedException | ExecutionException ignored) {
			}
			if (!result) return false;
		}
		return true;
	}
}
