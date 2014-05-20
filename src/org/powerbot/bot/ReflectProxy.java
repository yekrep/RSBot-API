package org.powerbot.bot;

public class ReflectProxy {
	public final Reflector reflector;
	public final Object obj;

	public ReflectProxy(final Reflector reflector, final Object obj) {
		this.reflector = reflector;
		this.obj = obj;
	}
}
