package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.PlayerMetaInfo;

/**
 * {@link Settings} is a static utility which provides raw access to the game's varpbits.
 *
 * @author Timer
 */
public class Settings {
	/**
	 * Returns the array of settings for the game.
	 *
	 * @return an array of the game's settings
	 */
	public static int[] getArray() {
		final Client client = ClientFactory.getFactory().getClient();
		final PlayerMetaInfo info;
		if (client == null || (info = client.getPlayerMetaInfo()) == null) return new int[0];
		final org.powerbot.client.Settings settings;
		final int[] data;
		if ((settings = info.getSettings()) == null || (data = settings.getData()) == null) return new int[0];
		return data.clone();
	}

	/**
	 * Returns the array of a specified index.
	 *
	 * @param index the index of the setting
	 * @return the setting for the specified index
	 */
	public static int get(final int index) {
		final int[] arr = getArray();
		return index < arr.length ? arr[index] : -1;
	}
}
