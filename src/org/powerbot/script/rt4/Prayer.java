package org.powerbot.script.rt4;

import org.powerbot.util.StringUtils;

public class Prayer extends ClientAccessor {
	public Prayer(final ClientContext ctx) {
		super(ctx);
	}

	public int points() {
		return StringUtils.parseInt(ctx.widgets.component(160, 15).text());
	}
}
