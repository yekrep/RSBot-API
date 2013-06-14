package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;

public abstract class LocatableIdNameQuery<K extends Locatable & Identifiable & Nameable> extends AbstractQuery<LocatableIdNameQuery<K>, K>
		implements Locatable.Query<LocatableIdNameQuery<K>>, Identifiable.Query<LocatableIdNameQuery<K>> {
	public LocatableIdNameQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected LocatableIdNameQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableIdNameQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
