package org.powerbot;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicReference;

public class Instrument {
	private static final AtomicReference<Instrumentation> instance = new AtomicReference<Instrumentation>(null);

	public static Instrumentation get() {
		return instance.get();
	}
}
