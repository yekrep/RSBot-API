package org.powerbot.nscript;

import java.util.Deque;
import java.util.EventListener;
import java.util.concurrent.FutureTask;

import org.powerbot.nscript.internal.ScriptContainer;

public interface Script extends Runnable, EventListener {
	public enum Event {
		START, SUSPEND, RESUME, STOP
	}

	public Deque<FutureTask<Boolean>> getTriggers(Event event);

	public void setContainer(ScriptContainer container);

	public ScriptContainer getContainer();
}
