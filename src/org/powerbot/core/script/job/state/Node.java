package org.powerbot.core.script.job.state;

import org.powerbot.core.concurrent.Task;

/**
 * @author Timer
 */
public abstract class Node extends Task {
	public abstract boolean activate();
}
