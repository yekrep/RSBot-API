package org.powerbot.concurrent;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class LinkedNonBlockingQueue<E> extends ConcurrentLinkedQueue<E> implements BlockingQueue<E> {
	private static final long serialVersionUID = 1L;

	@Override
	public void put(final E e) throws InterruptedException {
		offer(e);
	}

	@Override
	public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
		return offer(e);
	}

	@Override
	public E take() throws InterruptedException {
		return poll();
	}

	@Override
	public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
		return poll();
	}

	@Override
	public int remainingCapacity() {
		return Integer.MAX_VALUE - size();
	}

	@Override
	public int drainTo(final Collection<? super E> c) {
		return 0;
	}

	@Override
	public int drainTo(final Collection<? super E> c, int maxElements) {
		return 0;
	}
}
