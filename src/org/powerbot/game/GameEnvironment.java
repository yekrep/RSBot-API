package org.powerbot.game;

import org.powerbot.lang.LoadingException;

/**
 * Represents an environment in which the game can be started and terminated.
 * These calls are normally done threaded.
 *
 * @author Timer
 */
public interface GameEnvironment {
	/**
	 * Loads the game into this environment's memory.
	 *
	 * @return <tt>true</tt> if loading was successful; otherwise <tt>false</tt>.
	 * @throws org.powerbot.lang.LoadingException
	 *          The exception thrown to cause this method to return false.
	 */
	public boolean loadEnvironment() throws LoadingException;

	/**
	 * Kills this game and cleans up environment for re-initialization.
	 */
	public void killEnvironment();
}
