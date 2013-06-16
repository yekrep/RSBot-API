package org.powerbot.script;

import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.methods.MethodContext;

import java.util.EventListener;
import java.util.Queue;
import java.util.concurrent.Callable;

public interface Script extends Runnable, EventListener {
	public enum State {
		START, SUSPEND, RESUME, STOP
	}

	public Queue<Callable<Boolean>> getExecQueue(State state);

	public void setController(ScriptController container);

	public ScriptController getController();

	public void setContext(MethodContext ctx);

	public MethodContext getContext();
}
