package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

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
	 * {@inheritDoc}
	 */
	@Override
	public ItemQuery<K> id(int... ids) {
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
	public ItemQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}

	@Override
	public int count() {
		return size();
	}

	@Override
	public int count(boolean stacks) {
		if (!stacks) return count();
		int count = 0;
		for (Stackable stackable : this) count += stackable.getStackSize();
		return count;
	}
}
