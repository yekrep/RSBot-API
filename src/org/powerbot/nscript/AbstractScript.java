package org.powerbot.nscript;

import org.powerbot.nscript.internal.ScriptContainer;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

public abstract class AbstractScript implements Script {
	public final Logger log = Logger.getLogger(getClass().getName());
	private ScriptContainer container;
	private Map<Event, Deque<FutureTask<Boolean>>> triggers;

	public AbstractScript() {
		this.triggers = new ConcurrentHashMap<>();
		for (Event event : Event.values()) {
			this.triggers.put(event, new ConcurrentLinkedDeque<FutureTask<Boolean>>());
		}
	}

	@Override
	public Deque<FutureTask<Boolean>> getTriggers(Event event) {
		return triggers.get(event);
	}

	@Override
	public void setContainer(ScriptContainer container) {
		this.container = container;
	}

	@Override
	public ScriptContainer getContainer() {
		return this.container;
	}
}
