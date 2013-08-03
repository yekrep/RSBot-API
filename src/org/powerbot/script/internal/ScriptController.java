package org.powerbot.script.internal;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.internal.randoms.BankPin;
import org.powerbot.script.internal.randoms.Login;
import org.powerbot.script.internal.randoms.TicketDestroy;
import org.powerbot.script.internal.randoms.WidgetCloser;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Subscribable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

public final class ScriptController implements Runnable, Suspendable, Stoppable, Subscribable<EventListener> {
	private final MethodContext ctx;
	private final EventManager events;
	private final ExecutorService executor;
	private final List<Script> scripts;
	private final ScriptDefinition def;
	private final AtomicBoolean suspended, stopping;

	public ScriptController(final MethodContext ctx, final EventMulticaster multicaster, final Script script, final ScriptDefinition def) {
		this.ctx = ctx;
		events = new EventManager(multicaster);
		executor = new ScriptThreadExecutor(this);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		scripts = new ArrayList<>(5);
		scripts.add(new Login());
		scripts.add(new WidgetCloser());
		scripts.add(new TicketDestroy());
		scripts.add(new BankPin());
		scripts.add(script);

		this.def = def;
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

	public ExecutorService getExecutor() {
		return this.executor;
	}

	private void call(final Script.State state) {
		track(state);

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
	private void track(final Script.State state) {
		if (def == null || def.getName() == null || (!def.local && (def.getID() == null || def.getID().isEmpty()))) {
			return;
		}

		String action = "";

		switch (state) {
		case SUSPEND: action = "pause"; break;
		case RESUME: action = "resume"; break;
		case STOP: action = "stop"; break;
		}

		final String page = String.format("scripts/%s/%s", def.local ? "0/local" : def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
