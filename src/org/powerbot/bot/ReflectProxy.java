package org.powerbot.bot;

import java.lang.ref.SoftReference;

public class ReflectProxy {
	public final Reflector reflector;
	public final SoftReference<Object> obj;

	public ReflectProxy(final Reflector reflector, final Object obj) {
		this.reflector = reflector;
		this.obj = new SoftReference<Object>(obj);
	}
}
