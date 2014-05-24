package org.powerbot.bot;

import java.lang.ref.WeakReference;

public class ReflectProxy {
	public final Reflector reflector;
	public final WeakReference<Object> obj;

	public ReflectProxy(final Reflector reflector, final Object obj) {
		this.reflector = reflector;
		this.obj = new WeakReference<Object>(obj);
	}

	public boolean isTypeOf(final Class<? extends ReflectProxy> c) {
		final Object o = obj.get();
		final String s = reflector.getGroupClass(c.getName());
		if (o == null || s == null) {
			return false;
		}
		final Class<?> r = reflector.getClass(s);
		return r != null && o.getClass().isAssignableFrom(r);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof ReflectProxy)) {
			return false;
		}
		final Object obj = this.obj.get();
		return obj != null && obj == ((ReflectProxy) o).obj.get();
	}
}
