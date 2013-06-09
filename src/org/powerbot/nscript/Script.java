package org.powerbot.nscript;

import org.powerbot.nscript.lang.Stoppable;
import org.powerbot.nscript.lang.Suspendable;

import java.util.Collection;
import java.util.EventListener;
import java.util.concurrent.ExecutorService;

public interface Script extends Runnable, Suspendable, Stoppable, EventListener {
	public enum Event {
		START, SUSPEND, RESUME, STOP
	}

	public Collection<Runnable> getTriggers(Event event);

	public ExecutorService getExecutor();
}
