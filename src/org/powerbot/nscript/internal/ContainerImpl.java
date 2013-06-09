package org.powerbot.nscript.internal;

import java.util.concurrent.ExecutorService;

public class ContainerImpl implements ScriptContainer {
	private ScriptHandler handler;

	public ContainerImpl(ScriptHandler handler) {
		this.handler = handler;
	}

	@Override
	public ExecutorService getExecutor() {
		return handler.getExecutor();
	}

	@Override
	public boolean isStopping() {
		return handler.isStopping();
	}

	@Override
	public void stop() {
		handler.stop();
	}

	@Override
	public boolean isSuspended() {
		return handler.isSuspended();
	}

	@Override
	public void setSuspended(boolean suspended) {
		handler.setSuspended(suspended);
	}
}
