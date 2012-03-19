package org.powerbot.game.api;

import java.util.EventListener;
import java.util.logging.Logger;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.concurrent.action.Action;
import org.powerbot.concurrent.action.ActionExecutor;
import org.powerbot.event.EventManager;
import org.powerbot.game.bot.Bot;
import org.powerbot.lang.Activator;

import static org.powerbot.concurrent.action.ActionExecutor.State;

/**
 * @author Timer
 */
public abstract class ActiveScript implements EventListener {
	public final Logger log = Logger.getLogger(getClass().getName());

	private Activator stop_execution;
	private EventManager eventManager;
	private TaskContainer container;
	private ActionExecutor executor;

	public ActiveScript() {
		stop_execution = null;
		eventManager = null;
		container = null;
		executor = null;
	}

	public final void init(final Bot bot) {
		eventManager = bot.eventDispatcher;
		container = new TaskProcessor(bot.threadGroup);
		executor = new ActionExecutor(this.container);
	}

	protected final void registerAction(final Action action) {
		executor.append(action);
	}

	protected final void removeAction(final Action action) {
		executor.omit(action);
	}

	protected final void setStoppableExecution(final Activator activator) {
		this.stop_execution = activator;
	}

	protected abstract void setupJobs();

	public final Task start() {
		return new Task() {
			public void run() {
				setupJobs();
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

	public final Task stop() {
		return new Task() {
			public void run() {
				eventManager.remove(ActiveScript.this);
				executor.destroy();
			}
		};
	}

	protected State getState() {
		return executor.state;
	}

	public boolean isRunning() {
		return getState() != State.DESTROYED;
	}

	public boolean isPaused() {
		return getState() == State.LOCKED;
	}
}
