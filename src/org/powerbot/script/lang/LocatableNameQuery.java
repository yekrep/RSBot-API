package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;

public abstract class LocatableNameQuery<K extends Locatable & Nameable> extends AbstractQuery<LocatableNameQuery<K>, K>
		implements Locatable.Query<LocatableNameQuery<K>>, Nameable.Query<LocatableNameQuery<K>> {
	public LocatableNameQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected LocatableNameQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> name(String... names) {
		return select(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocatableNameQuery<K> name(Nameable... names) {
		return select(new Nameable.Matcher(names));
	}
}
