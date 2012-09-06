package org.powerbot.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Tree} in which provides states when polled.
 * <p/>
 * A {@link Tree} is designed for concurrency and will return a null state when polled ({@link org.powerbot.script.job.state.Tree#state()}), provided that you set the state ({@link org.powerbot.script.job.state.Tree#set(org.powerbot.script.job.state.Node)}).
 *
 * @author Timer
 */
public class Tree {
	private final Queue<Node> nodes;
	private final Object lock = new Object();

	private final AtomicReference<Node> current_node = new AtomicReference<>();

	/**
	 * A state {@link Tree} of {@link Node}s.
	 *
	 * @param nodes An array {@link Node}s which are provided as states in this {@link Tree}.
	 */
	public Tree(final Node[] nodes) {
		this.nodes = new ConcurrentLinkedQueue<>();
		this.nodes.addAll(Arrays.asList(nodes));
	}

	/**
	 * Determines the state of this {@link Tree}.
	 * <p/>
	 * Locks in attempt to be thread-safe.
	 * Checks if the {@link Node} provided is running before returning a new state.
	 *
	 * @return The state ({@link Node}) of this {@link Tree}.
	 */
	public final Node state() {
		synchronized (lock) {
			final Node stateNode = this.current_node.get();
			if (stateNode != null && stateNode.isAlive()) {
				return null;
			} else {
				for (final Node state : nodes) {
					if (state != null && state.activate()) {
						return state;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Sets the current node to the running state provided by this {@link Tree} ({@link org.powerbot.script.job.state.Tree#state()}).
	 *
	 * @param node The {@link Node} which is being processed as this {@link Tree}'s state.
	 */
	public final void set(final Node node) {
		current_node.set(node);
	}

	/**
	 * Gets the current (or last) processing {@link Node}.
	 *
	 * @return The current (or last) processing {@link Node}.
	 */
	public final Node get() {
		return current_node.get();
	}
}