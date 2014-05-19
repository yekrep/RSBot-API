package org.powerbot;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Instrument {
	private static final AtomicReference<Instrumentation> instance = new AtomicReference<Instrumentation>(null);

	public static Instrumentation get() {
		return instance.get();
	}

	public static List<Field> getFields(final Class<?> cls) {
		final List<Field> f = new ArrayList<Field>();
		Collections.addAll(f, cls.getDeclaredFields());

		final Class<?> p = cls.getSuperclass();
		if (p != null && !p.equals(Object.class)) {
			f.addAll(getFields(p));
		}

		return f;
	}
}
