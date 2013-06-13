package org.powerbot.script.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;

/**
 * @author Paris
 */
public abstract class AbstractQuery<T> extends ClientLink implements Iterable<T> {
	private ThreadLocal<List<T>> items;

	public AbstractQuery(final ClientFactory factory) {
		super(factory);
		items = new ThreadLocal<List<T>>() {
			@Override
		    protected List<T> initialValue() {
				return new CopyOnWriteArrayList<>(get());
			}
		};
	}

	protected abstract List<T> get();

	public AbstractQuery<T> select() {
		final List<T> items = this.items.get();

		synchronized (items) {
			items.clear();
			items.addAll(get());
		}

		return this;
	}

	public AbstractQuery<T> select(final Filter<T> f) {
		doSelect(f);
		return this;
	}

	protected void doSelect(final Filter<? super T> f) {
		final List<T> items = this.items.get();
		for (int i = 0; i < items.size(); i++) {
			if (!f.accept(items.get(i))) {
				items.remove(i);
			}
		}
	}

	public AbstractQuery<T> sort(final Comparator<? super T> c) {
		doSort(c);
		return this;
	}

	protected void doSort(final Comparator<? super T> c) {
		Collections.sort(this.items.get(), c);
	}

	public AbstractQuery<T> limit(final int count) {
		return limit(0, count);
	}

	public AbstractQuery<T> limit(final int offset, final int count) {
		final List<T> items = this.items.get();

		synchronized (items) {
			final List<T> range = new ArrayList<>(count);

			for (int i = offset; i < offset + count; i++) {
				range.add(items.get(i));
			}

			items.clear();
			items.addAll(range);
		}

		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return items.get().iterator();
	}

	public boolean isEmpty() {
		return items.get().isEmpty();
	}

	public boolean contains(final T t) {
		return items.get().contains(t);
	}

	public int size() {
		return items.get().size();
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) items.get().toArray();
	}

	public Deque<T> toDeque() {
		return new ConcurrentLinkedDeque<>(items.get());
	}

	public interface Filter<T> {
		public boolean accept(T t);
	}
}
