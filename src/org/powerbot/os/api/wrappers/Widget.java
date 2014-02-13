package org.powerbot.os.api.wrappers;

import org.powerbot.os.api.methods.ClientAccessor;
import org.powerbot.os.api.methods.ClientContext;

public class Widget extends ClientAccessor {
	private final int index;

	public Widget(final ClientContext ctx, final int index) {
		super(ctx);
		this.index = index;
	}
}
