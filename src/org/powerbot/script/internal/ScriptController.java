package org.powerbot.script.internal;

import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.internal.randoms.BankPin;
import org.powerbot.script.internal.randoms.Login;
import org.powerbot.script.internal.randoms.TicketDestroy;
import org.powerbot.script.internal.randoms.WidgetCloser;
import org.powerbot.script.lang.Prioritizable;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Subscribable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.MethodContext;

import java.util.Comparator;
import java.util.EventListener;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ScriptController implements Runnable, Suspendable, Stoppable, Subscribable<EventListener>, Prioritizable {
	private final MethodContext ctx;
	private final EventManager events;
	private PriorityManager priorityManager;
	private ExecutorService executor;
	private Queue<Script> scripts;
	private AtomicBoolean suspended;
	private AtomicBoolean stopping;

	public ScriptController(final MethodContext ctx, final EventMulticaster multicaster, final Script script) {
		this.ctx = ctx;
		events = new EventManager(multicaster);
		priorityManager = new PriorityManager(this);
		executor = new ScriptThreadExecutor(this);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		scripts = new PriorityQueue<>(5, new ScriptComparator());
		scripts.add(new Login());
		scripts.add(new WidgetCloser());
		scripts.add(new TicketDestroy());
		scripts.add(new BankPin());
		scripts.add(script);
	}

	@Override
	public void run() {
		for (final Script s : scripts) {
			s.setController(this);
			s.setContext(ctx);
			events.add(s);
			if (!s.getExecQueue(Script.State.START).contains(s)) {
				s.getExecQueue(Script.State.START).add(s);
			}
		}

		getExecutor().submit(priorityManager);
		call(Script.State.START);
		events.subscribeAll();
	}

	@Override
	public boolean isStopping() {
		return stopping.get();
	}

	@Override
	public void stop() {
		if (stopping.compareAndSet(false, true)) {
			call(Script.State.STOP);
			events.unsubscribeAll();
			executor.shutdown();
		}
	}

	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

	@Override
	public void suspend() {
		if (suspended.compareAndSet(false, true)) {
			call(Script.State.SUSPEND);
		}
	}

	@Override
	public void resume() {
		if (suspended.compareAndSet(true, false)) {
			call(Script.State.RESUME);
		}
	}

	@Override
	public void subscribe(final EventListener l) {
		events.subscribe(l);
	}

	@Override
	public void unsubscribe(final EventListener l) {
		events.unsubscribe(l);
	}

	@Override
	public int getPriority() {
		return priorityManager.getPriority();
	}

	public ExecutorService getExecutor() {
		return this.executor;
	}

	private void call(final Script.State state) {
		for (final Script s : scripts) {
			try {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						for (final Runnable task : s.getExecQueue(state)) {
							try {
								task.run();
							} catch (final Throwable ignored) {
							}
						}
					}
				});
			} catch (Exception ignored) {
			}
		}
	}

	Queue<Script> getScripts() {
		return scripts;
	}

	private final class ScriptComparator implements Comparator<Script> {
		@Override
		public int compare(final Script script1, final Script script2) {
			return script2.getPriority() - script1.getPriority();
		}
	}
}
