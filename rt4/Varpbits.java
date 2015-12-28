package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

/**
 * Varpbits
 */
public class Varpbits extends ClientAccessor {
	public Varpbits(final ClientContext ctx) {
		super(ctx);
	}

	public int[] array() {
		final int[] c = new int[0];
		final Client client = ctx.client();
		if (client == null) {
			return c;
		}
		final int[] varpbits = client.getVarpbits();
		return varpbits != null ? varpbits.clone() : c;
	}

	public int varpbit(final int index) {
		final int[] arr = array();
		return index > -1 && index < arr.length ? arr[index] : -1;
	}
}
