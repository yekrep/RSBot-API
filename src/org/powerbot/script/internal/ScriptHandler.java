package org.powerbot.script.internal;

import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptHandler implements Suspendable, Stoppable {
	EventManager eventManager;
	private ScriptContainer container;
	private ExecutorService executor;
	private AtomicReference<Script> script;
	private AtomicBoolean suspended;
	private AtomicBoolean stopping;

	public ScriptHandler(EventMulticaster multicaster) {
		this.eventManager = new EventManager(multicaster);
		this.container = new ContainerImpl(this);
		this.script = new AtomicReference<>(null);
		this.suspended = new AtomicBoolean(false);
		this.stopping = new AtomicBoolean(false);
	}

	public boolean start(Script script) {
		if (this.script.compareAndSet(null, script)) {
			suspended.set(false);
			stopping.set(false);
			this.executor = new ScriptExecutor(this);

			script.setContainer(container);
			eventManager.add(script);
			if (call(Script.Event.START)) {
				eventManager.subscribeAll();
				getExecutor().submit(script);
				return true;
			} else {
				eventManager.unsubscribeAll();
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
		if (!stopping.compareAndSet(false, true)) {
			return;
		}
		getExecutor().shutdown();
		//TODO ensure everything gets shutdown
	}

	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

	@Override
	public void suspend() {
		if (this.suspended.compareAndSet(false, true) && !call(Script.Event.SUSPEND)) {
			this.suspended.compareAndSet(true, false);
		}
	}

	@Override
	public void resume() {
		if (this.suspended.compareAndSet(true, false) && !call(Script.Event.RESUME)) {
			this.suspended.compareAndSet(false, true);
		}
	}

	public Script getScript() {
		return script.get();
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	private boolean call(Script.Event event) {
		Script script = this.script.get();
		if (script == null) {
			return false;
		}

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
			if (!result) {
				return false;
			}
		}
		return true;
	}
}
