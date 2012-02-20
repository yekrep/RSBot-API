package org.powerbot.lang;

/**
 * An exception that is thrown when loading a game environment.
 *
 * @author Timer
 */
public class LoadingException extends Exception {
	public LoadingException(String message) {
		super(message);
	}

	public LoadingException() {
		super();
	}
}
