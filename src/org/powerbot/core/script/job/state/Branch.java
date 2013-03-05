package org.powerbot.core.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Branch} is a {@link Node} which activates multiple children node when submitted, typically after activation ({@link org.powerbot.core.script.job.state.Branch#branch()}).
 * Activation of this {@link Branch} requires one {@link Node} to be considered active ({@link org.powerbot.core.script.job.state.Node#activate()}) in addition to {@link org.powerbot.core.script.job.state.Branch#branch()}.
 *
 * @author Timer
 */
@Deprecated
public abstract class Branch extends Node {
	private final Queue<Node> nodes;

	private final AtomicReference<Node> current_node = new AtomicReference<>();

	/**
	 * Constructs a {@link Branch}, which is a {@link Node} which activates a series of {@link Node}s when activated via {@link org.powerbot.core.script.job.state.Branch#branch()}.
	 *
	 * @param nodes The children {@link Node}s to activate when this {@link Branch} activates ({@link org.powerbot.core.script.job.state.Branch#branch()}).
	 */
	public Branch(final Node[] nodes) {
		this.nodes = new ConcurrentLinkedQueue<>();
		this.nodes.addAll(Arrays.asList(nodes));
	}

	/**
	 * Determines whether or not this branch should activate.
	 *
	 * @return <tt>true</tt> to activate the {@link Branch}; otherwise, <tt>false</tt>.
	 */
	public abstract boolean branch();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean activate() {
		if (branch()) {
			for (final Node child : nodes) {
				if (child != null && child.activate()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Executes the children {@link Node}s.
	 * This method will return if there is already a {@link Node} being processed under this {@link Branch}.
	 */
	@Override
	public final void execute() {
		final Node running_node = current_node.get();
		if (running_node != null && running_node.isAlive()) {
			return;
		}

		for (final Node node : nodes) {
			if (node != null && node.activate()) {
				current_node.set(node);
				getContainer().submit(node);
				node.join();
			}
		}
	}
}
