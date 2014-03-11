package org.powerbot.script.rs3;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;

public abstract class ItemQuery<K extends Identifiable & Nameable & Stackable> extends AbstractQuery<ItemQuery<K>, K, ClientContext>
		implements Identifiable.Query<ItemQuery<K>>, Nameable.Query<ItemQuery<K>>, Stackable.Query<ItemQuery<K>> {

	public ItemQuery(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ItemQuery<K> getThis() {
		return this;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count() {
		return size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count(final boolean stacks) {
		if (!stacks) {
			return count();
		}
		int count = 0;
		for (final Stackable stackable : this) {
			count += stackable.stackSize();
		}
		return count;
	}
}
