package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;

public abstract class PlayerQuery<K extends Locatable & Nameable> extends AbstractQuery<PlayerQuery<K>, K>
		implements Locatable.Query<PlayerQuery<K>>, Nameable.Query<PlayerQuery<K>> {
	public PlayerQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected PlayerQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> at(Locatable l) {
		return filter(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> within(Locatable target, double distance) {
		return filter(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> name(String... names) {
		return filter(new Nameable.Matcher(names));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerQuery<K> name(Nameable... names) {
		return filter(new Nameable.Matcher(names));
	}
}
