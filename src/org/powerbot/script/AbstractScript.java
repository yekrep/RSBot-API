package org.powerbot.script;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.powerbot.script.util.Prioritizable;
import org.powerbot.script.util.ScriptController;
import org.powerbot.script.xenon.util.Random;

/**
 * An abstract implementation of {@code Script}.
 *
 * @author Paris
 */
public abstract class AbstractScript implements Script, Prioritizable {
	protected final Logger log = Logger.getLogger(getClass().getName());
	private final Map<State, Collection<FutureTask<Boolean>>> tasks;
	private ScriptController controller;

	public AbstractScript() {
		tasks = new ConcurrentHashMap<State, Collection<FutureTask<Boolean>>>(State.values().length);

		for (final State state : State.values()) {
			tasks.put(state, new ArrayDeque<FutureTask<Boolean>>());
		}

		tasks.get(State.START).add(new FutureTask<Boolean>(this, true));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Collection<FutureTask<Boolean>> getTasks(final State state) {
		return tasks.get(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPriority(final int priority) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ScriptController getScriptController() {
		return controller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setScriptController(final ScriptController controller) {
		this.controller = controller;
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
