package org.powerbot.script.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.event.EventDispatcher;
import org.powerbot.event.debug.ViewMouse;
import org.powerbot.event.debug.ViewMouseTrails;
import org.powerbot.script.Script;
import org.powerbot.script.internal.scripts.Antipattern;
import org.powerbot.script.internal.scripts.BankPin;
import org.powerbot.script.internal.scripts.Login;
import org.powerbot.script.internal.scripts.StatTracker;
import org.powerbot.script.internal.scripts.TicketDestroy;
import org.powerbot.script.internal.scripts.WidgetCloser;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.service.scripts.ScriptBundle;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

public final class ScriptController implements Runnable, Script.Controller {
	private final MethodContext ctx;
	private final EventDispatcher dispatcher;
	private final BlockingDeque<Runnable> queue;
	private final ExecutorService executor;
	private final Queue<Script> scripts;
	private final Class<? extends Script>[] daemons;
	private final ScriptBundle bundle;
	private final AtomicReference<Script> script;
	private final AtomicBoolean started, suspended, stopping;
	private final Timer timeout;

	private final Runnable suspension;

	public ScriptController(final MethodContext ctx, final EventDispatcher dispatcher, final ScriptBundle bundle, final int timeout) {
		this.ctx = ctx;
		this.dispatcher = dispatcher;
		started = new AtomicBoolean(false);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		daemons = new Class[] {
				Login.class,
				WidgetCloser.class,
				TicketDestroy.class,
				BankPin.class,
				Antipattern.class,
				StatTracker.class,
		};
		scripts = new PriorityQueue<Script>(daemons.length + 1);
		script = new AtomicReference<Script>(null);

		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.NANOSECONDS, queue = new LinkedBlockingDeque<Runnable>());

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

		this.bundle = bundle;

		this.timeout = new Timer(timeout, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				((Timer) e.getSource()).stop();
				stop();
			}
		});
		this.timeout.setCoalesce(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (!started.compareAndSet(false, true)) {
			return;
		}

		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		if (!eq.isBlocking()) {
			eq.setBlocking(true);
		}

		if (!(dispatcher.contains(ViewMouse.class) || dispatcher.contains(ViewMouseTrails.class))) {
			dispatcher.add(new ViewMouseTrails());
		}

		for (final Class<? extends Script> d : daemons) {
			queue.offer(new ScriptBootstrap(d));
		}

		queue.offer(new ScriptBootstrap(bundle.script));

		queue.offer(new Runnable() {
			@Override
			public void run() {
				if (timeout.getDelay() > 0) {
					timeout.start();
				}

				call(Script.State.START);
			}
		});

		executor.submit(queue.poll());
	}

	private final class ScriptBootstrap implements Runnable {
		private final Class<? extends Script> clazz;

		public ScriptBootstrap(final Class<? extends Script> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void run() {
			final Script s;
			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Script.controllerProxy.put(ScriptController.this);
						} catch (final InterruptedException ignored) {
						}
					}
				}).start();
				s = clazz.newInstance();
				script.set(s);
			} catch (final Exception e) {
				e.printStackTrace();
				stop();
				return;
			}
			scripts.add(s);
			if (!s.getExecQueue(Script.State.START).contains(s)) {
				s.getExecQueue(Script.State.START).add(s);
			}
		}
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
			call(Script.State.STOP);
			executor.shutdown();
			try {
				if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (final InterruptedException ignored) {
			}

			final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
			if (eq.isBlocking()) {
				eq.setBlocking(false);
			}
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

			final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
			if (eq.isBlocking()) {
				eq.setBlocking(false);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume() {
		if (suspended.compareAndSet(true, false)) {
			call(Script.State.RESUME);

			final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
			if (!eq.isBlocking()) {
				eq.setBlocking(true);
			}
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
	 * {@inheritDoc}
	 */
	@Override
	public MethodContext getContext() {
		return ctx;
	}

	/**
	 * Returns the current script definition.
	 *
	 * @return the current script definition
	 */
	public ScriptDefinition getDefinition() {
		return bundle.definition;
	}

	/**
	 * Returns the primary {@link Script}.
	 *
	 * @return the primary {@link Script}
	 */
	public Script getScript() {
		return script.get();
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

		if (!queue.isEmpty()) {
			executor.submit(queue.poll());
		}
	}

	private void track(final Script.State state) {
		final ScriptDefinition def = getDefinition();
		if (def == null || def.getName() == null || (!def.local && (def.getID() == null || def.getID().isEmpty()))) {
			return;
		}

		String action = "";

		switch (state) {
		case START:
			break;
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

		final String page = String.format("scripts/%s/%s", def.local ? ScriptDefinition.LOCALID : def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
