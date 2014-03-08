package org.powerbot.script.os;

import org.powerbot.script.AbstractQuery;

public abstract class BasicQuery<K extends Locatable & Identifiable & Nameable> extends AbstractQuery<BasicQuery<K>, K, org.powerbot.script.os.ClientContext>
		implements Locatable.Query<BasicQuery<K>>, Identifiable.Query<BasicQuery<K>>,
		Nameable.Query<BasicQuery<K>> {
	public BasicQuery(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BasicQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> at(final Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> within(final double distance) {
		return within(ctx.players.local(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> within(final Locatable target, final double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> nearest() {
		return nearest(ctx.players.local());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> nearest(final Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> id(final int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> id(final int[]... ids) {
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
	public BasicQuery<K> id(final Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> name(final String... names) {
		return select(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicQuery<K> name(final Nameable... names) {
		return select(new Nameable.Matcher(names));
	}
}
