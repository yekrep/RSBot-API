package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSInterfaceBase extends ReflectProxy {
	public RSInterfaceBase(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSInterface[] getComponents() {
		final Object[] arr = reflector.access(this, Object[].class);
		final RSInterface[] arr2 = arr != null ? new RSInterface[arr.length] : new RSInterface[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSInterface(reflector, arr[i]);
			}
		}
		return arr2;
	}
}
