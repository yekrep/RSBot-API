package org.powerbot.script.rt6;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.Area;
import org.powerbot.script.Locatable;

/**
 * HintArrowQuery
 *
 * @param <K> the type of query which must extend {@link Locatable}
 */
public abstract class HintArrowQuery<K extends Locatable> extends AbstractQuery<HintArrowQuery<K>, K, ClientContext>
		implements Locatable.Query<HintArrowQuery<K>> {

	public HintArrowQuery(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HintArrowQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> at(final Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(final double radius) {
		return within(ctx.players.local(), radius);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(final Locatable locatable, final double radius) {
		return select(new Locatable.WithinRange(locatable, radius));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrowQuery<K> within(final Area area) {
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
	public HintArrowQuery<K> nearest(final Locatable locatable) {
		return sort(new Locatable.NearestTo(locatable));
	}
}
