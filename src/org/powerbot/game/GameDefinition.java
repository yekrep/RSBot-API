package org.powerbot.game;

import org.powerbot.lang.LoadingException;

/**
 * A definition of a <code>GameEnvironment</code> that manages all the data associated with this environment.
 *
 * @author Timer
 */
public class GameDefinition implements GameEnvironment {
	/**
	 * {@inheritDoc}
	 */
	public boolean loadEnvironment() throws LoadingException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
	}
}
