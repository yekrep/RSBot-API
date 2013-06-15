package org.powerbot.script.internal;

import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Subscribable;
import org.powerbot.script.lang.Suspendable;

import java.util.EventListener;
import java.util.concurrent.ExecutorService;

public interface ScriptGroup extends Subscribable<EventListener>, Suspendable, Stoppable {
	public ExecutorService getExecutor();
}
