package org.powerbot.core.script.job.state;

import org.powerbot.core.script.job.Task;

/**
 * A {@link Node} is an {@link Task} in which has an activation method ({@link org.powerbot.core.script.job.state.Node#activate()}).
 * A {@link Node} is generally deployed within a state environment (see {@link Tree}).
 * <p/>
 * The {@link Node} is not checked for activation when submitted.
 *
 * @author Timer
 */
@Deprecated
public abstract class Node extends Task {
	/**
	 * Determines whether or not to execute this {@link Node}.
	 *
	 * @return <tt>true</tt> to activate the {@link Node}; otherwise, <tt>false</tt>.
	 */
	public abstract boolean activate();
}
