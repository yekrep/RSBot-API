package org.powerbot.nscript.internal;

import java.util.concurrent.ExecutorService;

import org.powerbot.nscript.lang.Stoppable;
import org.powerbot.nscript.lang.Suspendable;

public interface ScriptContainer extends Suspendable, Stoppable {
	public ExecutorService getExecutor();
}
