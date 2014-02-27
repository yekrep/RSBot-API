package org.powerbot.script.rs3.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.RSHintArrow;
import org.powerbot.script.lang.HintArrowQuery;

/**
 * {@link HintArrows} is a utility which provides access to the game's hint (directional) arrows.
 *
 */
public class HintArrows extends HintArrowQuery<HintArrow> {
	public HintArrows(final MethodContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<HintArrow> get() {
		final List<HintArrow> items = new ArrayList<HintArrow>();

		final Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		final RSHintArrow[] arr = client.getRSHintArrows();
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow != null) {
				items.add(new HintArrow(ctx, arrow));
			}
		}
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HintArrow getNil() {
		return new HintArrow(ctx, null);
	}
}
