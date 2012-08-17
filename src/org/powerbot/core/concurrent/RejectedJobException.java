package org.powerbot.core.concurrent;

/**
 * An exception thrown by a {@link Container} when it is no longer accepting {@link Job}s.
 *
 * @author Timer
 */
public class RejectedJobException extends RuntimeException {
	public RejectedJobException(final String message) {
		super(message);
	}
}
