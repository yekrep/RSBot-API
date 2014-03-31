package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

public class Skills extends ClientAccessor {
	public Skills(final ClientContext ctx) {
		super(ctx);
	}

	public int[] getLevels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getLevels() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] getEffectiveLevels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getEffectiveLevels() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] getExperiences() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getExperiences() : new int[0];
		return arr != null ? arr : new int[0];
	}
}
