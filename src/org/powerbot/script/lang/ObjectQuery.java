package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

public abstract class ObjectQuery<K extends Locatable & Identifiable> extends AbstractQuery<ObjectQuery<K>, K>
		implements Locatable.Query<ObjectQuery<K>>, Identifiable.Query<ObjectQuery<K>> {
	public ObjectQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected ObjectQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectQuery<K> id(final int[]... ids) {
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
	public ObjectQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
