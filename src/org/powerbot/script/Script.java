package org.powerbot.script;

import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.methods.MethodContext;

import java.util.Deque;
import java.util.EventListener;
import java.util.concurrent.Callable;

public interface Script extends Runnable, EventListener {
	public enum Event {
		START, SUSPEND, RESUME, STOP
	}

	public Deque<Callable<Boolean>> getTriggers(Event event);

	public void setContainer(ScriptContainer container);

	public ScriptContainer getContainer();

	public void setContext(MethodContext methodContext);

	public MethodContext getContext();
}
