package org.powerbot.bot;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.ScriptBundle;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Client;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Script;
import org.powerbot.script.Validatable;

public final class ScriptController<C extends ClientContext<? extends Client>> extends ClientAccessor<C> implements Runnable, Validatable, Script.Controller {
	public static final String TIMEOUT_PROPERTY = "script.timeout", LOCAL_PROPERTY = "script.local";

	private final ThreadGroup group;
	private final AtomicReference<ThreadPoolExecutor> executor;
	private final List<Script> scripts;
	private final AtomicReference<Thread> timeout;
	private final Runnable suspension;
	private final AtomicBoolean started, suspended, stopping;
	public final AtomicLong[] times;

	public final List<Class<? extends Script>> daemons;
	public final AtomicReference<ScriptBundle> bundle;

	public ScriptController(final C ctx) {
		super(ctx);

		group = new ThreadGroup(ScriptThreadFactory.NAME);
		executor = new AtomicReference<ThreadPoolExecutor>(null);
		timeout = new AtomicReference<Thread>(null);
		started = new AtomicBoolean(false);
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);
		times = new AtomicLong[]{new AtomicLong(), new AtomicLong(), new AtomicLong(), new AtomicLong()};

		bundle = new AtomicReference<ScriptBundle>(null);
		daemons = new ArrayList<Class<? extends Script>>();
		scripts = new ArrayList<Script>(daemons.size() + 1);

		suspension = new Runnable() {
			@Override
			public void run() {
				while (isSuspended()) {
					try {
						Thread.sleep(600);
					} catch (final InterruptedException ignored) {
						Thread.yield();
						break;
					}
				}
			}
		};
	}

	@Override
	public boolean valid() {
		return started.get() && !stopping.get();
	}

	@Override
	public void run() {
		if (bundle.get() == null) {
			throw new IllegalStateException("bundle not set");
		}

		if (!started.compareAndSet(false, true)) {
			return;
		}

		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		if (!eq.isBlocking()) {
			eq.setBlocking(true);
		}

		final ClassLoader cl = bundle.get().script.getClassLoader();
		if (!(cl instanceof ScriptClassLoader)) {
			throw new SecurityException();
		}
		executor.set(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.NANOSECONDS, new LinkedBlockingDeque<Runnable>(), new ScriptThreadFactory(group, cl)));


		final String s = ctx.properties.getProperty(TIMEOUT_PROPERTY, "");
		if (!s.isEmpty()) {
			long l = 0;
			try {
				l = Long.parseLong(s);
			} catch (final NumberFormatException ignored) {
			}

			final long m = l + 1000;

			if (l > 0) {
				final Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(m);
						} catch (final InterruptedException ignored) {
							return;
						}
						track("timeout");
						stop();
					}
				});

				t.setPriority(Thread.MIN_PRIORITY);
				t.setDaemon(true);
				t.start();
				timeout.set(t);
			}
		}

		final BlockingQueue<Runnable> queue = executor.get().getQueue();

		for (final Class<? extends Script> d : daemons) {
			queue.offer(new ScriptBootstrap(d));
		}

		queue.offer(new ScriptBootstrap(bundle.get().script));

		queue.offer(new Runnable() {
			@Override
			public void run() {
				call(Script.State.START);
			}
		});

		executor.get().submit(queue.poll());
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
				s = clazz.newInstance();
				bundle.get().instance.set(s);
			} catch (final Exception e) {
				e.printStackTrace();
				stop();
				return;
			}
			scripts.add(s);
			ctx.bot().dispatcher.add(s);
		}
	}

	@Override
	public boolean isStopping() {
		return stopping.get() || executor.get() == null || executor.get().isShutdown();
	}

	@Override
	public void stop() {
		if (!(started.get() && stopping.compareAndSet(false, true))) {
			return;
		}

		final Thread t = timeout.getAndSet(null);
		if (t != null) {
			t.interrupt();
		}

		call(Script.State.STOP);
		ctx.bot().dispatcher.removeAll(ctx.dispatcher);
		ctx.dispatcher.clear();
		for (final Script s : scripts) {
			ctx.bot().dispatcher.remove(s);
		}
		executor.get().shutdown();
		executor.set(null);
		scripts.clear();

		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		if (eq.isBlocking()) {
			eq.setBlocking(false);
		}

		suspended.set(false);
		stopping.set(false);
		started.set(false);
	}

	@Override
	public boolean isSuspended() {
		return suspended.get();
	}

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

	@Override
	public boolean offer(final Runnable r) {
		return executor.get().getQueue().offer(r);
	}

	@Override
	public AbstractScript script() {
		return (AbstractScript) bundle.get().instance.get();
	}

	private void call(final Script.State state) {
		switch (state) {
		case START:
			times[Script.State.STOP.ordinal()].set(0L);
		case SUSPEND:
		case STOP:
			times[state.ordinal()].set(System.nanoTime());
			break;
		case RESUME:
			times[state.ordinal()].addAndGet(System.nanoTime() - times[Script.State.SUSPEND.ordinal()].getAndSet(0L));
			break;
		}

		final String t;
		switch (state) {
		case SUSPEND:
			t = "pause";
			break;
		case RESUME:
			t = "resume";
			break;
		case STOP:
			t = "stop";
			break;
		default:
		case START:
			t = "";
			break;
		}
		track(t);


		final BlockingQueue<Runnable> queue = executor.get().getQueue();

		for (final Script s : scripts) {
			for (final Runnable r : s.getExecQueue(state)) {
				queue.offer(r);
			}
		}

		if (state == Script.State.SUSPEND) {
			queue.offer(suspension);
		}

		if (!queue.isEmpty()) {
			executor.get().submit(queue.poll());
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ctx.bot().launcher.menu.get().scriptUpdate();
			}
		});
	}

	private void track(final String state) {
		final ScriptBundle.Definition def = bundle.get().definition;
		if (def == null || def.getName() == null || (!def.local && (def.getID() == null || def.getID().isEmpty()))) {
			return;
		}

		final String page = String.format("scripts/%s/%s", def.local ? ScriptBundle.Definition.LOCALID : def.getID(), state);
		GoogleAnalytics.getInstance().pageview(page, def.getName());
	}
}
