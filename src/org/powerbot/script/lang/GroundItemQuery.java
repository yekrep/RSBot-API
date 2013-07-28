package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

public abstract class GroundItemQuery<K extends Locatable & Identifiable & Nameable & Stackable> extends AbstractQuery<GroundItemQuery<K>, K>
		implements Locatable.Query<GroundItemQuery<K>>, Identifiable.Query<GroundItemQuery<K>>,
		Nameable.Query<GroundItemQuery<K>>, Stackable.Query<GroundItemQuery<K>> {
	public GroundItemQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected GroundItemQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> within(double distance) {
		return within(ctx.players.local(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> nearest() {
		return nearest(ctx.players.local());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> id(final int[]... ids) {
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
	public GroundItemQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> name(String... names) {
		return select(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItemQuery<K> name(Nameable... names) {
		return select(new Nameable.Matcher(names));
	}

	@Override
	public int count() {
		return size();
	}

	@Override
	public int count(boolean stacks) {
		if (!stacks) {
			return count();
		}
		int count = 0;
		for (Stackable stackable : this) {
			count += stackable.getStackSize();
		}
		return count;
	}
}
