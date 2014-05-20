package org.powerbot;

import java.lang.instrument.Instrumentation;

public class Instrument {
	private static Instrumentation instance;

	public static Instrumentation get() {
		return instance;
	}
}
