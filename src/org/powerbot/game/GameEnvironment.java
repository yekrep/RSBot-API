package org.powerbot.game;

import java.util.concurrent.Future;

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
	 */
	public Future<Object> initializeEnvironment();

	/**
	 * Kills this game and cleans up environment for re-initialization.
	 */
	public void killEnvironment();
}
