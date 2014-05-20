package org.powerbot.bot;

import java.lang.ref.SoftReference;

public class ReflectProxy {
	public final Reflector reflector;
	public final SoftReference<Object> obj;

	public ReflectProxy(final Reflector reflector, final Object obj) {
		this.reflector = reflector;
		this.obj = new SoftReference<Object>(obj);
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
