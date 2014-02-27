package org.powerbot.bot.rs3.tools;

import org.powerbot.bot.rs3.client.Node;

public class HashTable {
	private final org.powerbot.bot.rs3.client.HashTable nc;
	private Node curr;
	private int pos = 0;

	public HashTable(final org.powerbot.bot.rs3.client.HashTable nc) {
		if (nc == null) {
			throw new IllegalArgumentException();
		}
		this.nc = nc;
	}

	public Node getFirst() {
		pos = 0;
		return getNext();
	}

	public Node getNext() {
		final Node[] b = nc.getBuckets();
		if (b == null) {
			return null;
		}
		if (pos > 0 && pos <= b.length && b[pos - 1] != curr) {
			final Node n = curr;
			curr = n.getNext();
			return n;
		}
		while (pos < b.length) {
			final Node n = b[pos++].getNext();
			if (b[pos - 1] != n) {
				curr = n.getNext();
				return n;
			}
		}
		return null;
	}
}