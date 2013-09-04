package org.powerbot.script.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventListener;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import org.powerbot.Configuration;
import org.powerbot.event.EventMulticaster;
import org.powerbot.script.Script;
import org.powerbot.script.internal.scripts.Antipattern;
import org.powerbot.script.internal.scripts.BankPin;
import org.powerbot.script.internal.scripts.Login;
import org.powerbot.script.internal.scripts.TicketDestroy;
import org.powerbot.script.internal.scripts.WidgetCloser;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Subscribable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptBundle;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.HttpClient;

public final class ScriptController implements Runnable, Suspendable, Stoppable, Subscribable<EventListener> {
	private final MethodContext ctx;
	private final EventManager events;
	private final ExecutorService executor;
	private final Queue<Script> scripts;
	private final ScriptDefinition def;
	private final AtomicBoolean suspended, stopping;
	private final Timer timeout, login, session;
	private final AtomicReference<String> auth;
	private final AtomicLong started;

	public ScriptController(final MethodContext ctx, final EventMulticaster multicaster, final ScriptBundle bundle, final int timeout) {
		this.ctx = ctx;
		events = new EventManager(multicaster);
		executor = new ScriptThreadExecutor(this);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		scripts = new PriorityQueue<>(6);
		scripts.add(new Login());
		scripts.add(new WidgetCloser());
		scripts.add(new TicketDestroy());
		scripts.add(new BankPin());
		scripts.add(new Antipattern());
		scripts.add(bundle.script);

		this.def = bundle.definitiion;

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

		started = new AtomicLong(0);

		session = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				updateSession((int) (System.currentTimeMillis() / 1000L));
			}
		});
		session.setCoalesce(false);
	}

	public void updateSession(final int time) {
		try {
			HttpClient.openStream(Configuration.URLs.SCRIPTSSESSION, NetworkAccount.getInstance().getAuth(), def.getID(), started.get() / 1000L, Integer.toString(time)).close();
		} catch (final IOException ignored) {
		}
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

		if (timeout.getDelay() > 0) {
			timeout.start();
		}

		login.start();

		started.set(System.currentTimeMillis());
		if (def.session > 0) {
			final int d = def.session * 1000;
			session.setInitialDelay(d);
			session.setDelay(d);
			session.start();
			updateSession((int) (started.get() / 1000L));
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
			login.stop();
			session.stop();
			new Thread(new Runnable() {
				@Override
				public void run() {
					updateSession(1356998400);
				}
			}).start();

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
		return executor;
	}

	private void call(final Script.State state) {
		track(state);

		for (final Script s : scripts) {
			try {
				executor.submit(new RunnablePropagator(s.getExecQueue(state)));
			} catch (final Exception ignored) {
			}
		}
	}

	private static final class RunnablePropagator implements Runnable {
		private final Iterable<Runnable> tasks;

		public RunnablePropagator(final Iterable<Runnable> tasks) {
			this.tasks = tasks;
		}

		@Override
		public void run() {
			for (final Runnable task : tasks) {
				try {
					task.run();
				} catch (final Throwable ignored) {
				}
			}
		}
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
