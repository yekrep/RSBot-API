package org.powerbot.script;

import org.powerbot.script.internal.ScriptGroup;
import org.powerbot.script.methods.MethodContext;

import java.util.Deque;
import java.util.EventListener;
import java.util.concurrent.Callable;

public interface Script extends Runnable, EventListener {
	public enum State {
		START, SUSPEND, RESUME, STOP
	}

	public Deque<Callable<Boolean>> getStates(State state);

	public void setGroup(ScriptGroup container);

	public ScriptGroup getGroup();

	public void setContext(MethodContext ctx);

	public MethodContext getContext();
}
