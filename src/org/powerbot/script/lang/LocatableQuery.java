package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Locatable;

/**
 * @author Paris
 */
public abstract class LocatableQuery<T extends Locatable> extends AbstractQuery<T> {

	public LocatableQuery(final ClientFactory factory) {
		super(factory);
	}

	public LocatableQuery<T> at(final Locatable l) {
		doSelect(new Locatable.Matcher(l));
		return this;
	}

	public LocatableQuery<T> within(final double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	public LocatableQuery<T> within(final Locatable target, final double distance) {
		doSelect(new Locatable.WithinRange(target, distance));
		return this;
	}

	public LocatableQuery<T> nearest() {
		return nearest(ctx.players.getLocal());
	}

	public LocatableQuery<T> nearest(final Locatable target) {
		doSort(new Locatable.NearestTo(target));
		return this;
	}
}
