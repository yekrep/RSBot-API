package org.powerbot.core.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Timer
 */
public class Tree {
	private final Queue<Node> nodes;
	private final Object lock = new Object();

	private final AtomicReference<Node> current_node = new AtomicReference<>();

	public Tree(final Node[] nodes) {
		this.nodes = new ConcurrentLinkedQueue<>();
		this.nodes.addAll(Arrays.asList(nodes));
	}

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

	public final void set(final Node node) {
		current_node.set(node);
	}

	public final Node get() {
		return current_node.get();
	}
}