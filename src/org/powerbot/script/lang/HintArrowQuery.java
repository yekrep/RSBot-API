package org.powerbot.script.lang;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Locatable;

public abstract class HintArrowQuery<K extends Locatable> extends AbstractQuery<HintArrowQuery<K>, K>
		implements Locatable.Query<HintArrowQuery<K>> {
	public HintArrowQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected HintArrowQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(double distance) {
		return within(ctx.players.local(), distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(Area area) {
		return select(new Locatable.WithinArea(area));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> nearest() {
		return nearest(ctx.players.local());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}
}
