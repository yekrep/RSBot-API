package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.HintArrow;
import org.powerbot.bot.rt6.client.*;

import java.util.ArrayList;
import java.util.List;

/**
 * HintArrows
 * {@link HintArrows} is a utility which provides access to the game's hint (directional) arrows.
 */
public class HintArrows extends HintArrowQuery<org.powerbot.script.rt6.HintArrow> {
	public HintArrows(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<org.powerbot.script.rt6.HintArrow> get() {
		final List<org.powerbot.script.rt6.HintArrow> items = new ArrayList<>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		final HintArrow[] arr = client.getHintArrows();
		for (final HintArrow arrow : arr != null ? arr : new HintArrow[0]) {
			if (arrow.isNull()) {
				items.add(new org.powerbot.script.rt6.HintArrow(ctx, arrow));
			}
		}
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.powerbot.script.rt6.HintArrow nil() {
		return new org.powerbot.script.rt6.HintArrow(ctx, new HintArrow(ctx.client().reflector, null));
	}
}
