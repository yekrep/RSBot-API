package org.powerbot.core.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.core.Bot;
import org.powerbot.core.script.wrappers.HintArrow;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSHintArrow;

public class HintArrows {
	public static Set<HintArrow> getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new HashSet<>(0);

		final Set<HintArrow> arrows = new HashSet<>();
		final RSHintArrow[] arr = client.getRSHintArrows();
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow != null) arrows.add(new HintArrow(arrow));
		}
		return arrows;
	}
}
