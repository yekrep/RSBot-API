package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Locatable;

/**
 * @author Paris
 */
public abstract class LocatableQuery<K extends Locatable> extends AbstractQuery<LocatableQuery<K>, K> {

	public LocatableQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected LocatableQuery<K> getThis() {
		return this;
	}

	public LocatableQuery<K> at(final Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	public LocatableQuery<K> within(final double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	public LocatableQuery<K> within(final Locatable target, final double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	public LocatableQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	public LocatableQuery<K> nearest(final Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}
}
