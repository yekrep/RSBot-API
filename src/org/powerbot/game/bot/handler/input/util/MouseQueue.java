package org.powerbot.game.bot.handler.input.util;

import java.util.Arrays;

public class MouseQueue {
	private MouseNode[] queue;
	private volatile int size;
	private final Object lock;

	public MouseQueue(final int initialCapacity) {
		queue = new MouseNode[initialCapacity];
		size = 0;
		lock = new Object();
	}

	public int getSize() {
		return size;
	}

	public synchronized void offer(final MouseNode e) {
		synchronized (lock) {
			if (e == null) {
				throw new NullPointerException();
			}
			if (size++ >= queue.length) {
				grow(size);
			}
			queue[size - 1] = e;
		}
	}

	public synchronized void insert(final MouseNode e) {
		synchronized (lock) {
			if (e == null) {
				throw new NullPointerException();
			}
			if (size++ >= queue.length) {
				grow(size);
			}
			free(0);
			queue[0] = e;
		}
	}

	public synchronized MouseNode poll() {
		return poll(-1);
	}

	public synchronized MouseNode poll(int highestPriority) {
		synchronized (lock) {
			if (size == 0) {
				return null;
			}
			MouseNode best = null;
			int index = -1;
			for (int i = 0; i < size; i++) {
				final MouseNode node = queue[i];
				if (node.getPriority() > highestPriority) {
					best = node;
					highestPriority = node.getPriority();
					index = i;
				}
			}
			if (best != null) {
				remove(index);
				--size;
			}
			return best;
		}
	}

	public synchronized void condense() {
		synchronized (lock) {
			for (int i = 0; i < size; i++) {
				if (queue[i] == null || queue[i].isCanceled()) {
					remove(i);
					size--;
					i--;
				}
			}
		}
	}

	private void grow(final int minCapacity) {
		if (minCapacity < 0) {
			throw new OutOfMemoryError();
		}

		final int oldCapacity = queue.length;
		if (oldCapacity >= minCapacity) {
			return;
		}
		int newCapacity = ((oldCapacity < 8) ? ((oldCapacity + 1) * 2) : ((oldCapacity / 2) * 3));
		if (newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
		}
		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		queue = Arrays.copyOf(queue, newCapacity);
	}

	private void free(final int index) {
		System.arraycopy(queue, index, queue, index + 1, size - index);
		queue[index] = null;
	}

	private void remove(final int index) {
		final int length = size - index - 1;
		if (length > 0) {
			System.arraycopy(queue, index + 1, queue, index, length);
			queue[size - 1] = null;
		} else {
			queue[index] = null;
		}
	}
}
