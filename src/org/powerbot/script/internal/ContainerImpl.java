package org.powerbot.script.internal;

import java.util.EventListener;
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
	public void suspend() {
		handler.suspend();
	}

	@Override
	public void resume() {
		handler.resume();
	}

	@Override
	public void subscribe(EventListener eventListener) {
		handler.eventManager.subscribe(eventListener);
	}

	@Override
	public void unsubscribe(EventListener eventListener) {
		handler.eventManager.unsubscribe(eventListener);
	}
}
