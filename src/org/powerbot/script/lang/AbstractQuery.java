package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Paris
 */
public abstract class AbstractQuery<T extends AbstractQuery<T, K>, K> extends MethodProvider implements Iterable<K> {
	private ThreadLocal<List<K>> items;

	public AbstractQuery(final MethodContext factory) {
		super(factory);

		items = new ThreadLocal<List<K>>() {
			@Override
			protected List<K> initialValue() {
				return AbstractQuery.this.get();
			}
		};
	}

	protected abstract T getThis();

	protected abstract List<K> get();

	/**
	 * Resets this query to contain all the loaded elements.
	 */
	public T select() {
		final List<K> items = this.items.get();

		synchronized (items) {
			items.clear();
			items.addAll(get());
		}

		return getThis();
	}

	/**
	 * Sets this query's elements to a given collection.
	 *
	 * @param c a collection of types to set this query to contain
	 */
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

	/**
	 * Filters the current elements by the given filter.
	 *
	 * @param f the filter to apply to contained types
	 */
	public T select(final Filter<? super K> f) {
		final List<K> items = this.items.get();

		synchronized (items) {
			final List<K> remove = new ArrayList<>(items.size());

			for (final K k : items) {
				if (!f.accept(k)) {
					remove.add(k);
				}
			}

			items.removeAll(remove);
		}

		return getThis();
	}

	/**
	 * Sorts the current elements by a comparator.
	 *
	 * @param c the comparator
	 */
	public T sort(final Comparator<? super K> c) {
		final List<K> items = this.items.get();

		synchronized (items) {
			Collections.sort(items, c);
		}

		return getThis();
	}

	/**
	 * Shuffles the current collection.
	 */
	public T shuffle() {
		final List<K> items = this.items.get();

		synchronized (items) {
			Collections.shuffle(items);
		}

		return getThis();
	}

	/**
	 * Truncates the current collection to the maximum size.  Does not expand.
	 *
	 * @param count the maximum size
	 */
	public T limit(final int count) {
		return limit(0, count);
	}

	/**
	 * Truncates the current collection to the maximum size.  Does not expand.
	 *
	 * @param offset beginning element
	 * @param count  count of elements
	 */
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

	/**
	 * Truncates all elements except the first.
	 */
	public T first() {
		return limit(1);
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

	public List<K> toList() {
		return items.get();
	}
}
