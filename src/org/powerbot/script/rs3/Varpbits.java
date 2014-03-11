package org.powerbot.script.rs3;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.PlayerMetaInfo;

/**
 * {@link Varpbits} is a utility which provides raw access to the game's varpbits.
 *
 */
public class Varpbits extends ClientAccessor {
	public Varpbits(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the array of settings for the game.
	 *
	 * @return an array of the game's settings
	 */
	public int[] array() {
		final Client client = ctx.client();
		final PlayerMetaInfo info;
		if (client == null || (info = client.getPlayerMetaInfo()) == null) {
			return new int[0];
		}
		final org.powerbot.bot.rs3.client.Settings settings;
		final int[] data;
		if ((settings = info.getSettings()) == null || (data = settings.getData()) == null) {
			return new int[0];
		}
		return data.clone();
	}

	/**
	 * Returns the array of a specified index.
	 *
	 * @param index the index of the setting
	 * @return the setting for the specified index
	 */
	public int varpbit(final int index) {
		final int[] arr = array();
		return index < arr.length ? arr[index] : -1;
	}

	/**
	 * Gets the value at a given index and applies a given mask to the value.
	 *
	 * @param index the index in the settings array
	 * @param mask  the bitmask
	 * @return the masked value
	 */
	public int varpbit(final int index, final int mask) {
		return varpbit(index, 0, mask);
	}

	/**
	 * Gets the value at a given index, bit shifts it right by a given number of bits and applies a mask.
	 *
	 * @param index the index in the settings array
	 * @param shift the number of bits to right shift
	 * @param mask  the bitmask
	 * @return the masked value
	 */
	public int varpbit(final int index, final int shift, final int mask) {
		return (varpbit(index) >>> shift) & mask;
	}
}
