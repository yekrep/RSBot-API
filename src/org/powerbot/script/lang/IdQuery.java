package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

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
	 * {@inheritDoc}
	 */
	@Override
	public IdQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdQuery<K> id(int[]... ids) {
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
	public IdQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
