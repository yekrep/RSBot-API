package org.powerbot.nscript.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.nscript.Script;
import org.powerbot.nscript.lang.Stoppable;
import org.powerbot.nscript.lang.Suspendable;

public class ScriptHandler implements Suspendable, Stoppable {
	private ScriptContainer container;
	private ExecutorService executor;
	private AtomicReference<Script> script;
	private AtomicBoolean suspended;
	private AtomicBoolean stopping;

	public ScriptHandler() {
		this.container = new ContainerImpl(this);
		this.executor = new ScriptExecutor(this);
		this.script = new AtomicReference<>(null);
		this.suspended = new AtomicBoolean(false);
		this.stopping = new AtomicBoolean(false);
	}

	public boolean start(Script script) {
		if (this.script.compareAndSet(null, script)) {
			script.setContainer(container);
			if (call(Script.Event.START)) {
				getExecutor().submit(script);
				return true;
			} else {
				script.setContainer(null);
				this.script.set(null);
			}
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

		Collection<Callable<Boolean>> tasks = script.getTriggers(event);
		List<Callable<Boolean>> pending = new ArrayList<>(tasks.size());
		pending.addAll(tasks);

		ExecutorService service = getExecutor();
		for (Callable<Boolean> task : pending) {
			Future<Boolean> f = service.submit(task);
			boolean result = false;
			try {
				result = f.get();
			} catch (InterruptedException | ExecutionException ignored) {
			}
			if (!result) return false;
		}
		return true;
	}
}
