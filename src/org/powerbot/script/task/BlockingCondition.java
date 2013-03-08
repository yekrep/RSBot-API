package org.powerbot.script.task;

/**
 * Blocks thread execution until a condition has been satisfied.
 *
 * @author Paris
 */
public abstract class BlockingCondition implements Runnable {
	private final int pollFrequency;

	/**
	 * Creates a conditional block that polls every 600ms.
	 */
	public BlockingCondition() {
		this(600);
	}

	/**
	 * Creates a conditional block with a specified polling frequency.
	 *
	 * @param pollFrequency the polling frequency in milliseconds
	 */
	public BlockingCondition(final int pollFrequency) {
		this.pollFrequency = pollFrequency;
	}

	/**
	 * The condition to poll.
	 *
	 * @return {@code true} if the condition has been satisfied, otherwise {@code false} to continue blocking
	 */
	public abstract boolean pass();

	@Override
	public final void run() {
		while (!pass()) {
			try {
				Thread.sleep(pollFrequency);
			} catch (final InterruptedException ignored) {
			}
		}
	}
}
