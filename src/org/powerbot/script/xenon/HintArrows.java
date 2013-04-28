package org.powerbot.script.xenon;

import java.util.Arrays;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSHintArrow;
import org.powerbot.script.xenon.wrappers.HintArrow;

public class HintArrows {
	public static HintArrow[] getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new HintArrow[0];

		final RSHintArrow[] arr = client.getRSHintArrows();
		final HintArrow[] arrows = new HintArrow[arr != null ? arr.length : 0];
		int d = 0;
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow != null) arrows[d++] = new HintArrow(arrow);
		}
		return Arrays.copyOf(arrows, d);
	}
}
