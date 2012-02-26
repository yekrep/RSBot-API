package org.powerbot.lang;

/**
 * An exception that is thrown when loading a game environment.
 *
 * @author Timer
 */
public class LoadingException extends Exception {
	private static final long serialVersionUID = 1L;

	public LoadingException(String message) {
		super(message);
	}

	public LoadingException() {
		super();
	}
}
