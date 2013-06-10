package org.powerbot.nscript.internal;

import org.powerbot.nscript.lang.Stoppable;
import org.powerbot.nscript.lang.Subscribable;
import org.powerbot.nscript.lang.Suspendable;

import java.util.EventListener;
import java.util.concurrent.ExecutorService;

public interface ScriptContainer extends Subscribable<EventListener>, Suspendable, Stoppable {
	public ExecutorService getExecutor();
}
