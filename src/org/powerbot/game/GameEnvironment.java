package org.powerbot.game;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.powerbot.asm.NodeManipulator;
import org.powerbot.game.loader.AdaptException;

/**
 * Represents an environment in which the game can be started and terminated.
 * These calls are normally done threaded.
 *
 * @author Timer
 * @author Paris
 */
public interface GameEnvironment extends Closeable, Callable<Boolean> {
	/**
	 * Loads the game into this environment's memory.
	 *
	 * @return <tt>true</tt> if loading was successful; otherwise <tt>false</tt>.
	 */
	public Boolean call();

	/**
	 * @return The <code>NodeManipulator</code> for this environment.
	 * @throws org.powerbot.game.loader.AdaptException
	 *          Thrown when the node manipulation fails.
	 */
	NodeManipulator getNodeManipulator() throws AdaptException;

	/**
	 * Starts the loaded game environment.
	 *
	 * @return The future of this environment's starting task.
	 */
	public Future<?> start();

	/**
	 * Refreshes the gave environment.
	 */
	public void reload();

	/**
	 * Kills this game and cleans up environment for re-initialization.
	 */
	public void close();
}
