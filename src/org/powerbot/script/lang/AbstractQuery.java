package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;
import org.powerbot.script.util.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Paris
 */
public abstract class AbstractQuery<T extends AbstractQuery<T, K>, K> extends ClientLink implements Iterable<K> {
	private ThreadLocal<List<K>> items;

	public AbstractQuery(final ClientFactory factory) {
		super(factory);
		items = new ThreadLocal<List<K>>() {
			@Override
			protected List<K> initialValue() {
				return new CopyOnWriteArrayList<>(get());
			}
		};
	}

	protected abstract T getThis();

	protected abstract List<K> get();

	public T select() {
		final List<K> items = this.items.get();

		synchronized (items) {
			items.clear();
			items.addAll(get());
		}

		return getThis();
	}

	public T select(final Iterable<K> c) {
		final List<K> items = this.items.get();

		synchronized (items) {
			items.clear();
			for (final K item : c) {
				items.add(item);
			}
		}

		return getThis();
	}

	public T select(final Filter<? super K> f) {
		final List<K> items = this.items.get();
		List<K> remove = new ArrayList<>(items.size());
		for (K k : items) {
			if (!f.accept(k)) remove.add(k);
		}
		items.removeAll(remove);
		return getThis();
	}

	public T sort(final Comparator<? super K> c) {
		Collections.sort(this.items.get(), c);
		return getThis();
	}

	public T limit(final int count) {
		return limit(0, count);
	}

	public T limit(final int offset, final int count) {
		final List<K> items = this.items.get();

		synchronized (items) {
			final List<K> range = new ArrayList<>(count);
			final int c = Math.min(offset + count, items.size());

			for (int i = offset; i < c; i++) {
				range.add(items.get(i));
			}

			items.clear();
			items.addAll(range);
		}

		return getThis();
	}

	public K first() {
		// TODO: have a "nil" version of K so this doesn't return null
		if (size() < 1) return null;
		return items.get().get(0);
	}

	@Override
	public Iterator<K> iterator() {
		return items.get().iterator();
	}

	public boolean isEmpty() {
		return items.get().isEmpty();
	}

	public boolean contains(final K k) {
		return items.get().contains(k);
	}

	public int size() {
		return items.get().size();
	}

	public Deque<K> toDeque() {
		return new ConcurrentLinkedDeque<>(items.get());
	}
}
