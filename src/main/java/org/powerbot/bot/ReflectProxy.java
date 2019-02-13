package org.powerbot.bot;

import java.lang.ref.WeakReference;

public class ReflectProxy {
	public final Reflector reflector;
	public final WeakReference<Object> obj;

	public ReflectProxy(final Reflector reflector, final Object obj) {
		this.reflector = reflector;
		if (obj instanceof ReflectProxy) {
			this.obj = ((ReflectProxy) obj).obj;
		} else {
			this.obj = new WeakReference<>(obj);
		}
	}

	public boolean isTypeOf(final Class<? extends ReflectProxy> c) {
		return reflector.isTypeOf(obj.get(), c);
	}

	public boolean isNull() {
		return obj.get() == null;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(obj.get());
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
