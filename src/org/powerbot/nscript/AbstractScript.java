package org.powerbot.nscript;

import org.powerbot.nscript.internal.ScriptContainer;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.util.Random;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public abstract class AbstractScript implements Script {
	public final Logger log = Logger.getLogger(getClass().getName());
	protected ClientFactory ctx;
	private ScriptContainer container;
	private Map<Event, Deque<Callable<Boolean>>> triggers;

	public AbstractScript() {
		this.triggers = new ConcurrentHashMap<>();
		for (Event event : Event.values()) {
			this.triggers.put(event, new ConcurrentLinkedDeque<Callable<Boolean>>());
		}
	}

	@Override
	public final Deque<Callable<Boolean>> getTriggers(Event event) {
		return triggers.get(event);
	}

	@Override
	public final void setContainer(ScriptContainer container) {
		this.container = container;
	}

	@Override
	public final ScriptContainer getContainer() {
		return this.container;
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.ctx = clientFactory;
	}

	@Override
	public ClientFactory getClientFactory() {
		return ctx;
	}

	/**
	 * Sleeps for the specified duration.
	 *
	 * @param millis the duration in milliseconds.
	 */
	public void sleep(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
		}
	}

	/**
	 * Sleeps for a random duration between the specified intervals.
	 *
	 * @param min the minimum duration (inclusive)
	 * @param max the maximum duration (exclusive)
	 */
	public void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}

}
