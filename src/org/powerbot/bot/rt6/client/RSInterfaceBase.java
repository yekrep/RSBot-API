package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSInterfaceBase extends ContextAccessor {
	public RSInterfaceBase(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSInterface[] getComponents() {
		final Object[] arr = engine.access(this, Object[].class);
		final RSInterface[] arr2 = arr != null ? new RSInterface[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSInterface(engine, arr[i]);
			}
		}
		return arr2;
	}
}
