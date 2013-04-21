package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.PlayerMetaInfo;

public class Settings {
	public static final int BANK_TAB = 110;

	public static int[] getArray() {
		final Client client = Bot.client();
		if (client == null) return new int[0];

		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final org.powerbot.game.client.Settings settings;
		final int[] data;
		if (info == null || (settings = info.getSettings()) == null || (data = settings.getData()) == null)
			return new int[0];
		return data.clone();
	}

	public static int get(final int index) {
		final int[] arr = getArray();
		return index < arr.length ? arr[index] : -1;
	}
}
