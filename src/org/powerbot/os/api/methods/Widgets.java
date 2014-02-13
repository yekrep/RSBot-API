package org.powerbot.os.api.methods;

import org.powerbot.os.api.wrappers.Widget;

public class Widgets extends ClientAccessor {
	public Widgets(final ClientContext ctx) {
		super(ctx);
	}

	public Widget get(final int index) {
		return new Widget(ctx, index);
	}
}
