package org.powerbot.core.concurrent;

/**
 * An exception thrown when a living {@link Job} is attempted to be worked upon.
 *
 * @author Timer
 */
public class DuplicateJobException extends RuntimeException {
	public DuplicateJobException(final String message) {
		super(message);
	}
}
