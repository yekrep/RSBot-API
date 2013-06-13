package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Locatable;

public abstract class LocatableQuery<K extends Locatable> extends AbstractQuery<LocatableQuery<K>, K>
		implements Locatable.Query<LocatableQuery<K>> {
	public LocatableQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected LocatableQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}
}
