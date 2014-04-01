package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

public class Skills extends ClientAccessor {
	public Skills(final ClientContext ctx) {
		super(ctx);
	}

	public int[] levels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillLevels2() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] effectiveLevels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillLevels1() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] experiences() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillExps() : new int[0];
		return arr != null ? arr : new int[0];
	}
}
