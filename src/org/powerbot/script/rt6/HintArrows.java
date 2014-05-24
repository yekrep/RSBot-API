package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSHintArrow;

/**
 * {@link HintArrows} is a utility which provides access to the game's hint (directional) arrows.
 */
public class HintArrows extends HintArrowQuery<HintArrow> {
	public HintArrows(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<HintArrow> get() {
		final List<HintArrow> items = new ArrayList<HintArrow>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		final RSHintArrow[] arr = client.getRSHintArrows();
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow.isNull()) {
				items.add(new HintArrow(ctx, arrow));
			}
		}
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrow nil() {
		return new HintArrow(ctx, null);
	}
}
