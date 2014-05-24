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
		if (o == null) {
			return false;
		}
		Class<?> m = o.getClass();

		final String s = c.getName().replace('.', '/');
		String i;
		do {
			i = reflector.getGroup(m.getName());
			m = m.getSuperclass();
		} while (m != Object.class && !s.equals(i));
		return s.equals(i);
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
