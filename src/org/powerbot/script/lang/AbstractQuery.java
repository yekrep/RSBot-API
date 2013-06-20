package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An abstract implementation of a chaining query-based data set filter which is thread safe.
 *
 * @param <T> the super class
 * @param <K> the subject type
 *
 * @author Paris
 */
public abstract class AbstractQuery<T extends AbstractQuery<T, K>, K> extends MethodProvider implements Iterable<K> {
	private final ThreadLocal<List<K>> items;
	private final Method set;

	/**
	 * Creates a base {@link AbstractQuery}.
	 *
	 * @param factory the {@link MethodContext} to associate with
	 */
	public AbstractQuery(final MethodContext factory) {
		super(factory);

		items = new ThreadLocal<List<K>>() {
			@Override
			protected List<K> initialValue() {
				return new CopyOnWriteArrayList<>(AbstractQuery.this.get());
			}
		};

		Method set = null;
		try {
			set = CopyOnWriteArrayList.class.getMethod("setArray", Object[].class);
		} catch (final NoSuchMethodException ignored) {
		}
		this.set = set;
	}

	/**
	 * Returns {@code this}.
	 *
	 * @return must always return {@code this}
	 */
	protected abstract T getThis();

	/**
	 * Returns a fresh data set.
	 *
	 * @return a new data set for subsequent queries
	 */
	protected abstract List<K> get();

	/**
	 * Selects a fresh data set into the query cache.
	 *
	 * @return {@code this} for the purpose of chaining
	 */
	public T select() {
		final List<K> items = this.items.get(), a = get();
		setArray(items, a);
		return getThis();
	}

	/**
	 * Selects the specified data set into the query cache.
	 *
	 * @param c a {@link List}, {@link Collection} or any other {@link Iterable}
	 *             source of items to replace the existing cache with
	 * @return {@code this} for the purpose of chaining
	 */
	public T select(final Iterable<K> c) {
		final List<K> items = this.items.get(), a = new ArrayList<>();
		for (final K k : c) {
			a.add(k);
		}
		setArray(items, a);
		return getThis();
	}

	/**
	 * Selects the items which satisfy the condition of the specified
	 * {@link Filter} into the query cache.
	 *
	 * @param f the condition
	 * @return {@code this} for the purpose of chaining
	 */
	public T select(final Filter<? super K> f) {
		final List<K> items = this.items.get(), a = new ArrayList<>(items.size());
		for (final K k : items) {
			if (f.accept(k)) {
				a.add(k);
			}
		}
		setArray(items, a);
		return getThis();
	}

	/**
	 * Sorts the items in the query cache by the specified {@link Comparator}.
	 *
	 * @param c the comparator
	 * @return {@code this} for the purpose of chaining
	 */
	public T sort(final Comparator<? super K> c) {
		final List<K> items = this.items.get(), a = new ArrayList<>(items);
		Collections.sort(a, c);
		setArray(items, a);
		return getThis();
	}

	/**
	 * Sorts the items in the query cache by a random rearrangement.
	 *
	 * @return {@code this} for the purpose of chaining
	 */
	public T shuffle() {
		final List<K> items = this.items.get(), a = new ArrayList<>(items);
		Collections.shuffle(a);
		setArray(items, a);
		return getThis();
	}

	private void setArray(final List<K> a, final List<K> c) {
		try {
			if (set != null) {
				set.invoke(a, c.toArray());
				return;
			}
		} catch (final IllegalAccessException | InvocationTargetException ignored) {
		}

		a.clear();
		a.addAll(c);
	}

	/**
	 * Limits the query cache to the specified number of items.
	 *
	 * @param count the maximum number of items to retain
	 * @return {@code this} for the purpose of chaining
	 */
	public T limit(final int count) {
		return limit(0, count);
	}

	/**
	 * Limits the query cache to the items within the specified bounds.
	 *
	 * @param offset the starting index
	 * @param count the maximum number of items to retain
	 * @return {@code this} for the purpose of chaining
	 */
	public T limit(final int offset, final int count) {
		final List<K> items = this.items.get(), a = new ArrayList<>(count);
		final int c = Math.min(offset + count, items.size());
		for (int i = offset; i < c; i++) {
			a.add(items.get(i));
		}
		setArray(items, a);
		return getThis();
	}

	/**
	 * Limits the query cache to the first item (if any).
	 *
	 * @return {@code this} for the purpose of chaining
	 */
	public T first() {
		return limit(1);
	}

	/**
	 * Adds every item in the query cache to the specified {@link Collection}.
	 *
	 * @param c the {@link Collection} to add to
	 * @return {@code this} for the purpose of chaining
	 */
	public T addTo(final Collection<? super K> c) {
		c.addAll(items.get());
		return getThis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<K> iterator() {
		return items.get().iterator();
	}

	public T each(final ChainingIterator<K> c) {
		int i = 0;
		for (final K k : this) {
			if (!c.next(i++, k)) {
				break;
			}
		}

		return getThis();
	}

	/**
	 * Returns {@code true} if the query cache contains no items.
	 *
	 * @return {@code true} if the query cache contains no items
	 */
	public boolean isEmpty() {
		return items.get().isEmpty();
	}

	/**
	 * Returns {@code true} if the query cache contains the specified item.
	 *
	 * @param k item whose presence in this query cache is to be tested
	 * @return {@code true} if the query cache contains the specified item
	 */
	public boolean contains(final K k) {
		return items.get().contains(k);
	}

	/**
	 * Returns the number of items in the query cache.
	 *
	 * @return the number of items in the query cache
	 */
	public int size() {
		return items.get().size();
	}
}
