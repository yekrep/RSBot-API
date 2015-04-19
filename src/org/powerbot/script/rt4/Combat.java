package org.powerbot.script.rt4;

import org.powerbot.util.StringUtils;

public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	public int health() {
		return StringUtils.parseInt(ctx.widgets.component(548, 77).text());
	}

	public int prayerPoints() {
		return StringUtils.parseInt(ctx.widgets.component(548, 87).text());
	}
}
