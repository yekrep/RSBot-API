package org.powerbot.script.internal;

import org.powerbot.script.ExecutorDispatch;
import org.powerbot.script.ScriptController;

public final class AbstractScriptController implements ScriptController {
	private final ScriptManager manager;

	public AbstractScriptController(final ScriptManager manager) {
		this.manager = manager;
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
}

