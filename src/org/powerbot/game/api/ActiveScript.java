package org.powerbot.game.api;

import java.util.EventListener;

import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.action.Action;
import org.powerbot.concurrent.action.ActionExecutor;
import org.powerbot.event.EventManager;
import org.powerbot.game.bot.Bot;
import org.powerbot.lang.Activator;

/**
 * @author Timer
 */
public abstract class ActiveScript implements EventListener {
	private Activator stop_execution;
	private EventManager eventManager;
	private TaskContainer container;
	private ActionExecutor executor;

	public ActiveScript() {
		this.stop_execution = null;
		this.eventManager = null;
		this.container = null;
		this.executor = null;
	}

	public final void init(final Bot bot) {
		this.eventManager = bot.eventDispatcher;
		this.container = bot.processor;
		this.executor = new ActionExecutor(this.container);
	}

	protected final void registerWorker(final Action action) {
		executor.append(action);
	}

	protected final void destroyWorker(final Action action) {
		executor.omit(action);
	}

	protected final void setStoppableExecution(final Activator activator) {
		this.stop_execution = activator;
	}

	protected abstract void registerWorkers();

	public final Runnable start() {
		return new Runnable() {
			public void run() {
				registerWorkers();
				resume();
			}
		};
	}

	public final void resume() {
		eventManager.accept(ActiveScript.this);
		executor.listen();
	}

	public final void pause() {
		pause(false);
	}

	public final void pause(final boolean removeListener) {
		executor.lock();
		if (removeListener) {
			eventManager.remove(ActiveScript.this);
		}
	}

	public final Runnable stop() {
		return new Runnable() {
			public void run() {
				eventManager.remove(ActiveScript.this);
				executor.destroy();
			}
		};
	}
}
