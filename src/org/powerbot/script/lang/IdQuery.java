package org.powerbot.script.lang;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Identifiable;

public abstract class IdQuery<K extends Identifiable> extends AbstractQuery<IdQuery<K>, K>
		implements Identifiable.Query<IdQuery<K>> {
	public IdQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected IdQuery<K> getThis() {
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
	public <K extends Identifiable> IdQuery<K> union(final IdQuery<K> q) {
		return new IdQuery<K>(ctx) {
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
	public IdQuery<K> id(final int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdQuery<K> id(final int[]... ids) {
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
	public IdQuery<K> id(final Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
