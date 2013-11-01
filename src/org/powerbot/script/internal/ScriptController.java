package org.powerbot.script.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import org.powerbot.Configuration;
import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.internal.scripts.Antipattern;
import org.powerbot.script.internal.scripts.BankPin;
import org.powerbot.script.internal.scripts.Break;
import org.powerbot.script.internal.scripts.Login;
import org.powerbot.script.internal.scripts.TicketDestroy;
import org.powerbot.script.internal.scripts.WidgetCloser;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptBundle;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.HttpClient;

public final class ScriptController implements Runnable, Script.Controller {
	private final MethodContext ctx;
	private final EventManager events;
	private final BlockingDeque<Runnable> queue;
	private final ExecutorService executor;
	private final Queue<Script> scripts;
	private final ScriptDefinition def;
	private final AtomicBoolean suspended, stopping;
	private final Timer timeout, login;
	private final AtomicReference<String> auth;

	private final Runnable empty, suspension;

	public ScriptController(final MethodContext ctx, final EventMulticaster multicaster, final ScriptBundle bundle, final int timeout) {
		this.ctx = ctx;
		events = new EventManager(multicaster);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		scripts = new PriorityQueue<>(6);
		scripts.add(new Login());
		scripts.add(new WidgetCloser());
		scripts.add(new TicketDestroy());
		scripts.add(new BankPin());
		scripts.add(new Antipattern());
		scripts.add(bundle.script);
		//scripts.add(new Break());

		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.NANOSECONDS, queue = new LinkedBlockingDeque<>());

		empty = new Runnable() {
			@Override
			public void run() {
				Thread.yield();
			}
		};
		suspension = new Runnable() {
			@Override
			public void run() {
				while (isSuspended()) {
					try {
						Thread.sleep(600);
					} catch (final InterruptedException ignored) {
					}
				}
			}
		};

		this.def = bundle.definition;

		this.timeout = new Timer(timeout, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				((Timer) e.getSource()).stop();
				stop();
			}
		});
		this.timeout.setCoalesce(false);

		auth = new AtomicReference<>(NetworkAccount.getInstance().getAuth());

		login = new Timer(NetworkAccount.REVALIDATE_INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final String a = NetworkAccount.getInstance().getAuth();
				if (!auth.getAndSet(a).equals(a)) {
					((Timer) e.getSource()).stop();
					stop();
				}
			}
		});
		login.setCoalesce(false);
	}

	/**
	 * {@inheritDoc}
	 */
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

		if (timeout.getDelay() > 0) {
			timeout.start();
		}

		login.start();

		call(Script.State.START);
		events.subscribeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStopping() {
		return stopping.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		if (stopping.compareAndSet(false, true)) {
			login.stop();

			call(Script.State.STOP);
			events.unsubscribeAll();
			executor.shutdown();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void suspend() {
		if (suspended.compareAndSet(false, true)) {
			call(Script.State.SUSPEND);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume() {
		if (suspended.compareAndSet(true, false)) {
			call(Script.State.RESUME);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BlockingDeque<Runnable> getExecutor() {
		return queue;
	}

	/**
	 * Returns the current script definition.
	 *
	 * @return the current script definition
	 */
	public ScriptDefinition getDefinition() {
		return def;
	}

	private void call(final Script.State state) {
		track(state);

		for (final Script s : scripts) {
			for (final Runnable r : s.getExecQueue(state)) {
				queue.offer(r);
			}
		}

		if (state == Script.State.SUSPEND) {
			queue.offer(suspension);
		}

		executor.submit(empty);
	}

	private void track(final Script.State state) {
		if (def == null || def.getName() == null || (!def.local && (def.getID() == null || def.getID().isEmpty()))) {
			return;
		}

		String action = "";

		switch (state) {
		case SUSPEND: {
			action = "pause";
			break;
		}
		case RESUME: {
			action = "resume";
			break;
		}
		case STOP: {
			action = "stop";
			break;
		}
		}

		final String page = String.format("scripts/%s/%s", def.local ? "0/local" : def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
