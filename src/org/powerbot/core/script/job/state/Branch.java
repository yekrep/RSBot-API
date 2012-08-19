package org.powerbot.core.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Timer
 */
public abstract class Branch extends Node {
	private final Queue<Node> nodes;

	private final AtomicReference<Node> current_node = new AtomicReference<>();

	public Branch(final Node[] nodes) {
		this.nodes = new ConcurrentLinkedQueue<>();
		this.nodes.addAll(Arrays.asList(nodes));
	}

	public abstract boolean branch();

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
