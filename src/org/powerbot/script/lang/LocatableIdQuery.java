package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;

public abstract class LocatableIdQuery<K extends Locatable & Identifiable> extends AbstractQuery<LocatableIdQuery<K>, K>
		implements Locatable.Query<LocatableIdQuery<K>>, Identifiable.Query<LocatableIdQuery<K>> {
	public LocatableIdQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected LocatableIdQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
