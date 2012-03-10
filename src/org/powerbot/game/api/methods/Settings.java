package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.SettingsData;

/**
 * @author Timer
 */
public class Settings {
	public static int[] get() {
		return ((int[]) ((SettingsData) Bot.resolve().client.getSettingArray()).getSettingsData()).clone();
	}

	public static int get(final int index) {
		final int[] settings = get();
		if (index < settings.length) {
			return settings[index];
		}
		return -1;
	}
}
