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
 * @author Paris
 */
public abstract class AbstractQuery<T extends AbstractQuery<T, K>, K> extends MethodProvider implements Iterable<K> {
	private final ThreadLocal<List<K>> items;
	private final Method set;

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

	protected abstract T getThis();

	protected abstract List<K> get();

	/**
	 * Resets this query to contain all the loaded elements.
	 */
	public T select() {
		final List<K> items = this.items.get(), a = get();
		setArray(items, a);
		return getThis();
	}

	/**
	 * Sets this query's elements to a given collection.
	 *
	 * @param c a collection of types to set this query to contain
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
	 * Filters the current elements by the given filter.
	 *
	 * @param f the filter to apply to contained types
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
	 * Sorts the current elements by a comparator.
	 *
	 * @param c the comparator
	 */
	public T sort(final Comparator<? super K> c) {
		final List<K> items = this.items.get(), a = new ArrayList<>(items);
		Collections.sort(a, c);
		setArray(items, a);
		return getThis();
	}

	/**
	 * Shuffles the current collection.
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
		final List<K> items = this.items.get(), a = new ArrayList<>(count);
		final int c = Math.min(offset + count, items.size());
		for (int i = offset; i < c; i++) {
			a.add(items.get(i));
		}
		setArray(items, a);
		return getThis();
	}

	/**
	 * Truncates all elements except the first.
	 */
	public T first() {
		return limit(1);
	}

	public T addTo(final Collection<? super K> c) {
		c.addAll(items.get());
		return getThis();
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
}
