package org.powerbot.bot.rt6;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.HardReference;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.SoftReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTable<N> implements Iterator<N>, Iterable<N> {
	private final org.powerbot.bot.rt6.client.HashTable table;
	private final Class<N> type;
	private int bucket_index = 0;
	private Node curr;
	private Node next;

	public HashTable(final org.powerbot.bot.rt6.client.HashTable table, final Class<N> type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.table = table;
		this.type = type;
	}

	@Override
	public Iterator<N> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}
		final Node[] buckets = !table.isNull() ? table.getBuckets() : null;
		if (buckets == null) {
			return false;
		}
		final Object c = curr != null ? curr.obj.get() : null;
		if (bucket_index > 0 && bucket_index <= buckets.length && buckets[bucket_index - 1].obj.get() != c) {
			next = curr;
			curr = curr.getNext();
			return true;
		}
		while (bucket_index < buckets.length) {
			final Node n = buckets[bucket_index++].getNext();
			if (buckets[bucket_index - 1].obj.get() != n.obj.get()) {
				next = n;
				curr = n.getNext();
				return true;
			}
		}
		return false;
	}

	@Override
	public N next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final Constructor<N> c;
		try {
			c = type.getDeclaredConstructor(Reflector.class, Object.class);
		} catch (final NoSuchMethodException e) {
			return null;
		}
		N n = null;
		try {
			n = c.newInstance(table.reflector, next.obj.get());
		} catch (final NullPointerException | InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
		}
		next = null;
		return n;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static <E extends ReflectProxy> E lookup(final org.powerbot.bot.rt6.client.HashTable table, final long id, final Class<E> type) {
		if (table == null) {
			return null;
		}
		final Constructor<E> c;
		try {
			c = type.getDeclaredConstructor(Reflector.class, Object.class);
		} catch (final NoSuchMethodException e) {
			return null;
		}
		final Node[] buckets = table.getBuckets();
		if (buckets.length == 0) {
			try {
				return c.newInstance(table.reflector, null);
			} catch (final InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
			}
			return null;
		}
		final Node n = buckets[(int) (id & buckets.length - 1)];
		for (Node o = n.getNext(); !o.equals(n) && !o.isNull(); o = o.getNext()) {
			if (o.getId() == id) {
				Object e = o;
				if (o.isTypeOf(SoftReference.class)) {
					e = ((java.lang.ref.SoftReference<?>) new SoftReference(o.reflector, o).get()).get();
				} else if (o.isTypeOf(HardReference.class)) {
					e = new HardReference(o.reflector, o).get();
				}
				try {
					return c.newInstance(table.reflector, e);
				} catch (final InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
				}
			}
		}
		try {
			return c.newInstance(table.reflector, null);
		} catch (final InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
		}
		return null;
	}
}

