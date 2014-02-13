package org.powerbot.os.api.methods;

import java.util.Arrays;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.api.wrappers.Widget;

public class Widgets extends ClientAccessor {
	private Widget[] sparseCache;

	public Widgets(final ClientContext ctx) {
		super(ctx);
	}

	public synchronized Widget get(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Widget c = new Widget(ctx, index);
		if (index >= sparseCache.length) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
		}
		return sparseCache[index] = c;
	}
}
