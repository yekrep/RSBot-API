package org.powerbot.script.rt4;

import org.powerbot.util.StringUtils;

public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	public int health() {
		return StringUtils.parseInt(ctx.widgets.component(160, 5).text());
	}
}
