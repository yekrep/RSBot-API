package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

public class Skills extends ClientAccessor {
	public Skills(final ClientContext ctx) {
		super(ctx);
	}

	public int[] levels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getLevels() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] effectiveLevels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getEffectiveLevels() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] experiences() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getExperiences() : new int[0];
		return arr != null ? arr : new int[0];
	}
}
