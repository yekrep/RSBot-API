package org.powerbot.core.script.internal;

import org.powerbot.game.client.Node;

public class HashTable {
	private org.powerbot.game.client.HashTable nc;
	private Node current;
	private int c_index = 0;

	public HashTable(final org.powerbot.game.client.HashTable hashTable) {
		nc = hashTable;
	}

	public Node getFirst() {
		c_index = 0;
		return getNext();
	}

	public Node getNext() {
		if (c_index > 0 && nc.getBuckets()[c_index - 1] != current) {
			Node node = current;
			current = node.getNext();
			return node;
		}
		while (c_index < nc.getBuckets().length) {
			Node node = nc.getBuckets()[c_index++].getNext();
			if (nc.getBuckets()[c_index - 1] != node) {
				current = node.getNext();
				return node;
			}
		}
		return null;
	}
}
