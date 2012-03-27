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
import org.powerbot.lang.Activatable;

import static org.powerbot.concurrent.action.ActionExecutor.State;

/**
 * @author Timer
 */
public abstract class ActiveScript implements EventListener {
	public final Logger log = Logger.getLogger(getClass().getName());

	private Activatable stop_execution;
	private EventManager eventManager;
	private TaskContainer container;
	private ActionExecutor executor;

	private Bot bot;
	private boolean silent;

	public ActiveScript() {
		stop_execution = null;
		eventManager = null;
		container = null;
		executor = null;
		silent = false;
	}

	public final void init(final Bot bot) {
		this.bot = bot;
		eventManager = bot.getEventDispatcher();
		container = new TaskProcessor(bot.threadGroup);
		executor = new ActionExecutor(container, bot.getContainer());
	}

	protected final void provide(final Action action) {
		executor.append(action);
	}

	protected final void revoke(final Action action) {
		executor.omit(action);
	}

	protected final void submit(final Task task) {
		container.submit(task);
	}

	protected final void setStoppableExecution(final Activatable activator) {
		this.stop_execution = activator;
	}

	protected abstract void setup();

	public final Task start() {
		return new Task() {
			public void run() {
				setup();
				resume();
				if (bot != null) {
					bot.ensureAntiRandoms();
				}
			}
		};
	}

	public final void resume() {
		silent = false;
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

	public final void silentLock(final boolean removeListener) {
		silent = true;
		pause(removeListener);
	}

	public final void stop() {
		eventManager.remove(ActiveScript.this);
		executor.destroy();
		container.shutdown();
	}

	public final void kill() {
		container.stop();
	}

	protected State getState() {
		return executor.state;
	}

	public boolean isRunning() {
		return getState() != State.DESTROYED;
	}

	public boolean isPaused() {
		return isLocked() && !silent;
	}

	public boolean isLocked() {
		return getState() == State.LOCKED;
	}

	public boolean isSilentlyLocked() {
		return silent;
	}

	public TaskContainer getContainer() {
		return container;
	}
}
