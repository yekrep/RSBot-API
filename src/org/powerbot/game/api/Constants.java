package org.powerbot.game.api;

import java.util.Map;

public class Constants {
	public final int
			CLIENTSTATE_3, CLIENTSTATE_6, CLIENTSTATE_7,
			CLIENTSTATE_9, CLIENTSTATE_10, CLIENTSTATE_11,
			CLIENTSTATE_12;

	public Constants(final Map<Integer, Integer> constants) {
		CLIENTSTATE_3 = constants.get(3);
		CLIENTSTATE_6 = constants.get(6);
		CLIENTSTATE_7 = constants.get(7);
		CLIENTSTATE_9 = constants.get(9);
		CLIENTSTATE_10 = constants.get(10);
		CLIENTSTATE_11 = constants.get(11);
		CLIENTSTATE_12 = constants.get(12);
	}
}
