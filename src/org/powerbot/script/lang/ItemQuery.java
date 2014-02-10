package org.powerbot.script.lang;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Nameable;
import org.powerbot.script.wrappers.Stackable;

public abstract class ItemQuery<K extends Identifiable & Nameable & Stackable> extends AbstractQuery<ItemQuery<K>, K>
		implements Identifiable.Query<ItemQuery<K>>, Nameable.Query<ItemQuery<K>>, Stackable.Query<ItemQuery<K>> {
	public ItemQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected ItemQuery<K> getThis() {
		return this;
	}

	/**
	 * Merges another query of the same type into this one and stores combined result
	 * internally as an immutable set for future calls to {@link #select()}.
	 *
	 * @param q the query to merge in (i.e. append)
	 * @param <K> the result type of the new query
	 * @return a new query of the combined results, which are immutable
	 */
	public <K extends Identifiable & Nameable & Stackable> ItemQuery<K> union(final ItemQuery<K> q) {
		return new ItemQuery<K>(ctx) {
			@Override
			protected List<K> get() {
				final List<K> items = new ArrayList<K>();
				addTo(items);
				q.addTo(items);
				return items;
			}

			@Override
			public K getNil() {
				return q.getNil();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> id(final int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> id(final int[]... ids) {
		int z = 0;

		for (final int[] x : ids) {
			z += x.length;
		}

		final int[] a = new int[z];
		int i = 0;

		for (final int[] x : ids) {
			for (final int y : x) {
				a[i++] = y;
			}
		}

		return select(new Identifiable.Matcher(a));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> id(final Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> name(final String... names) {
		return select(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> name(final Nameable... names) {
		return select(new Nameable.Matcher(names));
	}

	@Override
	public int count() {
		return size();
	}

	@Override
	public int count(final boolean stacks) {
		if (!stacks) {
			return count();
		}
		int count = 0;
		for (final Stackable stackable : this) {
			count += stackable.getStackSize();
		}
		return count;
	}
}
