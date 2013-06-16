package org.powerbot.bot;

import org.powerbot.client.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.internal.EventManager;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.internal.randoms.BankPin;
import org.powerbot.script.internal.randoms.Login;
import org.powerbot.script.internal.randoms.TicketDestroy;
import org.powerbot.script.internal.randoms.WidgetCloser;
import org.powerbot.script.methods.MethodContext;

import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BotScriptController implements ScriptController {
	private final MethodContext ctx;
	private final EventManager events;
	private ExecutorService executor;
	private Collection<Script> scripts;
	private AtomicBoolean suspended;
	private AtomicBoolean stopping;

	public BotScriptController(final MethodContext ctx, final EventMulticaster multicaster, final Script script) {
		this.ctx = ctx;
		events = new EventManager(multicaster);
		executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60l, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		scripts = new LinkedList<>();
		scripts.add(new Login());
		scripts.add(new WidgetCloser());
		scripts.add(new TicketDestroy());
		scripts.add(new BankPin());
		scripts.add(script);
	}

	@Override
	public void run() {
		for (final Script s : scripts) {
			s.setContext(ctx);
			events.add(s);
		}

		call(Script.State.START);
	}

	@Override
	public boolean isStopping() {
		return stopping.get();
	}

	@Override
	public void stop() {
		if (stopping.compareAndSet(false, true)) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					call(Script.State.STOP);
				}
			});
			executor.shutdown();
			events.unsubscribeAll();
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

	private void call(final Script.State state) {
		if (executor.isShutdown()) {
			return;
		}

		for (final Script s : scripts) {
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
		}
	}
}
