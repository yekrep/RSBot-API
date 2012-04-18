package org.powerbot.game.bot.handler.input;

import java.awt.EventQueue;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.handler.input.util.MouseNode;
import org.powerbot.game.bot.handler.input.util.MouseQueue;

public class MouseReactor implements Task {
	private final TaskContainer container;
	private final MouseQueue mouseQueue;
	private final MouseExecutor executor;

	public MouseReactor(final Bot bot) {
		this.container = bot.getContainer();
		mouseQueue = new MouseQueue(0);
		executor = new MouseExecutor(bot, this);
	}

	@Override
	public void run() {
		/* While the bot is running... */
		while (!container.isShutdown()) {
			/* Remove cancelled or nullified tasks (i.e. human cancelled). */
			mouseQueue.condense();
			/* Poll the next highest priority mouse task. */
			final MouseNode node = mouseQueue.poll();
			if (node != null) {
				final Timer timer = node.getTimer();
				/* Step until timeout or consumes itself. */
				while (timer.isRunning() && node.processable()) {
					executor.step(node);
				}
				/* If the node timed out, cancel it and keep it removed. */
				if (!timer.isRunning()) {
					node.cancel();
					/* Notify the lock to release on the thread and return false. */
					synchronized (node.getLock()) {
						node.getLock().notify();
					}
				}
				continue;
			}

			/* Wait if nothing is left. */
			synchronized (this) {
				try {
					wait(500);
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}

	public boolean process(final MouseNode node) {
		/* Offer the node to the queue for processing by the reactor. */
		mouseQueue.offer(node);
		/* Notify the reactor to wake up. */
		synchronized (this) {
			notify();
		}
		/* Lock on the thread's lock until completion (failure or completed). */
		if (!EventQueue.isDispatchThread()) {
			synchronized (node.getLock()) {
				try {
					node.getLock().wait();
				} catch (final InterruptedException ignored) {
				}
			}
		}
		return node.isCompleted();
	}

	public MouseQueue getQueue() {
		return mouseQueue;
	}
}
