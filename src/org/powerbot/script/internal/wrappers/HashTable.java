package org.powerbot.script.internal.wrappers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.powerbot.game.client.Node;

public class HashTable implements Iterator<Node>, Iterable<Node> {
	private final org.powerbot.game.client.HashTable nc;
	private volatile Node current;
	private volatile int cursor;

	public HashTable(final org.powerbot.game.client.HashTable hashTable) {
		nc = hashTable;
		cursor = 0;
	}

	public void reset() {
		cursor = 0;
	}

	public Node first() {
		reset();
		return next();
	}

	@Override
	public boolean hasNext() {
		return cursor < nc.getBuckets().length;
	}

	@Override
	public Node next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final Node[] b = nc.getBuckets();
		if (cursor > 0 && b[cursor - 1] != current) {
			final Node n = current;
			current = n.getNext();
			return n;
		}
		while (cursor < b.length) {
			final Node p = b[cursor], n = b[cursor++].getNext();
			if (p != n) {
				current = n.getNext();
				return n;
			}
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Node> iterator() {
		return this;
	}
}
