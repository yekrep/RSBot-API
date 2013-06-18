package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

public abstract class NpcQuery<K extends Locatable & Identifiable & Nameable> extends AbstractQuery<NpcQuery<K>, K>
		implements Locatable.Query<NpcQuery<K>>, Identifiable.Query<NpcQuery<K>>,
		Nameable.Query<NpcQuery<K>> {
	public NpcQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected NpcQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> id(final int[]... ids) {
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
	public NpcQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> name(String... names) {
		return select(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NpcQuery<K> name(Nameable... names) {
		return select(new Nameable.Matcher(names));
	}
}
