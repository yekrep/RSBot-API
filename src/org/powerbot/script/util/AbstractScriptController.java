package org.powerbot.script.util;

import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptDefinition;
import org.powerbot.script.internal.ScriptManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AbstractScriptController implements ScriptController {
	private final ScriptManager manager;
	private final Queue<Script> queue;

	public AbstractScriptController(final ScriptManager manager) {
		this.manager = manager;
		queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public boolean isStopping() {
		return manager.isStopping();
	}

	@Override
	public void stop() {
		manager.stop();
	}

	@Override
	public boolean isSuspended() {
		return manager.isSuspended();
	}

	@Override
	public void suspend() {
		manager.suspend();
	}

	@Override
	public void resume() {
	}

	@Override
	public ExecutorDispatch<Boolean> getExecutorService() {
		return manager;
	}

	@Override
	public Queue<Script> getLockQueue() {
		return queue;
	}

	@Override
	public Iterable<ScriptDefinition> getScripts() {
		return manager.getScripts();
	}
}

