package org.powerbot.script.internal;

import java.util.EventListener;
import java.util.concurrent.ExecutorService;

import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Subscribable;
import org.powerbot.script.lang.Suspendable;

public interface ScriptController extends Runnable, Suspendable, Stoppable, Subscribable<EventListener> {
	public ExecutorService getExecutor();
}
