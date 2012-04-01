package org.powerbot.game.api;

import java.util.EventListener;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.powerbot.concurrent.Processor;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.concurrent.ThreadPool;
import org.powerbot.concurrent.strategy.Policy;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.event.EventManager;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;

import static org.powerbot.concurrent.strategy.StrategyDaemon.State;

/**
 * @author Timer
 */
public abstract class ActiveScript implements EventListener, Processor {
	public final Logger log = Logger.getLogger(getClass().getName());

	private Policy stop_execution;
	private EventManager eventManager;
	private TaskContainer container;
	private StrategyDaemon executor;

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
		executor = new StrategyDaemon(container, bot.getContainer());
	}

	protected final void provide(final Strategy strategy) {
		executor.append(strategy);
	}

	protected final void revoke(final Strategy strategy) {
		executor.omit(strategy);
	}

	public final Future<?> submit(final Task task) {
		return container.submit(task);
	}

	protected final void setStoppableExecution(final Policy policy) {
		this.stop_execution = policy;
	}

	protected final void setIterationSleep(final int milliseconds) {
		executor.setIterationSleep(milliseconds);
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
		container.submit(new Task() {
			public void run() {
				onStop();
			}
		});
		eventManager.remove(ActiveScript.this);
		executor.destroy();
		container.shutdown();

		final String name = Thread.currentThread().getThreadGroup().getName();
		if (name.startsWith(GameDefinition.THREADGROUPNAMEPREFIX) ||
				name.startsWith(ThreadPool.THREADGROUPNAMEPREFIX)) {
			BotChrome.getInstance().toolbar.updateScriptControls();
		}
	}

	public void onStop() {
	}

	public final void kill() {
		container.stop();
	}

	protected final State getState() {
		return executor.state;
	}

	public final boolean isRunning() {
		return getState() != State.DESTROYED;
	}

	public final boolean isPaused() {
		return isLocked() && !silent;
	}

	public final boolean isLocked() {
		return getState() == State.LOCKED;
	}

	public final boolean isSilentlyLocked() {
		return silent;
	}

	public final TaskContainer getContainer() {
		return container;
	}
}
