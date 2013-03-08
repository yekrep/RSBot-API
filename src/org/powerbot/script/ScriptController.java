package org.powerbot.script;

import java.util.concurrent.ExecutorService;

public interface ScriptController extends Stoppable, Suspendable {

	public ExecutorService getExecutorService();
}
