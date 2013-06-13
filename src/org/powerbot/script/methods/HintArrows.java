package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.powerbot.client.Client;
import org.powerbot.client.RSHintArrow;
import org.powerbot.script.lang.BasicQuery;
import org.powerbot.script.wrappers.HintArrow;

/**
 * {@link HintArrows} is a static utility which provides access to the game's hint (directional) arrows.
 *
 * @author Timer
 */
public class HintArrows extends BasicQuery<HintArrow> {
	public HintArrows(ClientFactory factory) {
		super(factory);
	}

	/**
	 * Returns the loaded {@link HintArrow}s.
	 *
	 * @return an array of loaded {@link HintArrow}s
	 */
	@Override
	protected List<HintArrow> get() {
		final List<HintArrow> items = new ArrayList<>();

		Client client = ctx.getClient();
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
}
