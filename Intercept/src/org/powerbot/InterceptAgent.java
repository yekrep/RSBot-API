package org.powerbot;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

public class InterceptAgent {

	public static void premain(final String agentArgs, final Instrumentation instrumentation) {
		try {
			final Field f = Class.forName(InterceptAgent.class.getPackage().getName() + ".Instrument").getDeclaredField("instance");
			final boolean a = f.isAccessible();
			f.setAccessible(true);
			AtomicReference.class.getDeclaredMethod("set", new Class[]{Object.class}).invoke(f.get(null), instrumentation);
			f.setAccessible(a);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unused")
	public static void agentmain(final String agentArgs, final Instrumentation instrumentation) {
		premain(agentArgs, instrumentation);
	}
}
