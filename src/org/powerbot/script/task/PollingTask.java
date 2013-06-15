package org.powerbot.script.task;

import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public abstract class PollingTask extends ClientLink implements Runnable, Suspendable, Stoppable {
	public Logger log = Logger.getLogger(getClass().getName());
	private final ScriptContainer container;
	private AtomicBoolean suspended, stopping;

	public PollingTask(Script script) {
		this(script.getClientFactory(), script.getContainer());
	}

	public PollingTask(ClientFactory ctx, ScriptContainer container) {
		super(ctx);
		this.container = container;
		this.suspended = new AtomicBoolean(false);
		this.stopping = new AtomicBoolean(false);
	}

	public abstract int poll();

	@Override
	public final void run() {
		stopping.set(false);
		while (!isStopping()) {
			int sleep;
			try {
				if (isSuspended()) {
					sleep = 600;
				} else {
					sleep = poll();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				sleep = -1;
			}

			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ignored) {
				}
			} else if (sleep == -1) {
				break;
			}
		}
		stop();
	}

	public ScriptContainer getContainer() {
		return this.container;
	}

	@Override
	public boolean isStopping() {
		return getContainer().isStopping() || stopping.get();
	}

	@Override
	public void stop() {
		if (stopping.compareAndSet(false, true)) {
		}
	}

	@Override
	public boolean isSuspended() {
		return getContainer().isSuspended() || suspended.get();
	}

	@Override
	public void suspend() {
		if (suspended.compareAndSet(false, true)) {
		}
	}

	@Override
	public void resume() {
		if (suspended.compareAndSet(true, false)) {
		}
	}
}
