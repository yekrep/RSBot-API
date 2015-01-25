package org.powerbot.bot;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reflector {
	private final ClassLoader loader;
	private final Map<String, String> interfaces;
	private final Map<String, FieldConfig> configs;
	private final Map<String, Class<?>> cache1;
	private final Map<FieldConfig, java.lang.reflect.Field> cache2;

	public Reflector(final ClassLoader loader, final ReflectorSpec spec) {
		this(loader, spec.interfaces, spec.configs);
	}

	public Reflector(final ClassLoader loader, final Map<String, String> interfaces, final Map<String, FieldConfig> configs) {
		this.loader = loader;
		this.interfaces = interfaces;
		this.configs = configs;
		cache1 = new HashMap<String, Class<?>>();
		cache2 = new HashMap<FieldConfig, java.lang.reflect.Field>();
	}

	public static class FieldConfig {
		private final String parent, name, type;
		private final long multiplier;

		public FieldConfig(final String parent, final String name, final String type, final long multiplier) {
			this.parent = parent;
			this.name = name;
			this.type = type;
			this.multiplier = multiplier;
		}
	}

	public boolean accessBool(final ReflectProxy accessor) {
		return access(accessor, false);
	}

	public int accessInt(final ReflectProxy accessor) {
		final FieldConfig f = getFieldConfig();
		if (f == null) {
			return -1;
		}
		final Integer i = access(accessor, f, Integer.class);
		return i != null ? i * (int) f.multiplier : -1;
	}

	public int[] accessInts(final ReflectProxy accessor) {
		return access(accessor, int[].class);
	}

	public long accessLong(final ReflectProxy accessor) {
		final FieldConfig f = getFieldConfig();
		if (f == null) {
			return -1l;
		}
		final Long j = access(accessor, f, Long.class);
		return j != null ? j * (int) f.multiplier : -1l;
	}

	public float accessFloat(final ReflectProxy accessor) {
		return access(accessor, -1f);
	}

	public byte accessByte(final ReflectProxy accessor) {
		return access(accessor, (byte) -1);
	}

	public short accessShort(final ReflectProxy accessor) {
		return access(accessor, (short) -1);
	}

	public String accessString(final ReflectProxy accessor) {
		return access(accessor, String.class);
	}

	public Object access(final ReflectProxy accessor) {
		return access(accessor, Object.class);
	}

	public <T> T access(final ReflectProxy accessor, final Class<T> t) {
		return access(accessor, getFieldConfig(), t);
	}

	private <T> T access(final ReflectProxy accessor, final FieldConfig r, final Class<T> t) {
		final Object p = accessor.obj.get();
		if (p == null || r == null) {
			return null;
		}

		final java.lang.reflect.Field f;
		if (cache2.containsKey(r)) {
			f = cache2.get(r);
			if (f == null) {
				return null;
			}
		} else {
			final Class<?> c = getClass(r.parent);
			if (c == null) {
				cache2.put(r, null);
				return null;
			}
			try {
				f = c.getDeclaredField(r.name);
			} catch (final NoSuchFieldException ignored) {
				cache2.put(r, null);
				return null;
			}
			f.setAccessible(true);
		}

		final Object o;
		try {
			o = f.get((f.getModifiers() & Modifier.STATIC) != 0 ? null : p);
		} catch (final IllegalAccessException ignored) {
			return null;
		}
		return o != null ? t.cast(o) : null;
	}

	public <T> T access(final ReflectProxy accessor, final T d) {
		@SuppressWarnings("unchecked")
		final T v = (T) access(accessor, d.getClass());
		return v == null ? d : v;
	}

	private StackTraceElement getCallingAPI() {
		final String n = Reflector.class.getName();
		final StackTraceElement[] arr = Thread.currentThread().getStackTrace();
		for (int i = 2; i < arr.length; i++) {
			if (arr[i] == null || arr[i].getClassName().equals(n)) {
				continue;
			}
			return arr[i];
		}
		return arr[arr.length - 1];
	}

	String getGroup(final String c) {
		return interfaces.get(c);
	}

	String getGroupClass(final String g) {
		final String g2 = g.replace('.', '/');
		for (final Map.Entry<String, String> e : interfaces.entrySet()) {
			if (e.getValue().equals(g2)) {
				return e.getKey();
			}
		}
		return null;
	}

	Class<?> getClass(final String s) {
		final Class<?> c;//TODO
		if (cache1.containsKey(s)) {
			c = cache1.get(s);
		} else {
			try {
				cache1.put(s, c = loader.loadClass(s));
			} catch (final ClassNotFoundException ignored) {
				cache1.put(s, null);
				return null;
			}
		}
		return c;
	}

	public boolean isTypeOf(final Object o, final Class<? extends ReflectProxy> c) {
		final String s = getGroupClass(c.getName());
		if (o == null || s == null) {
			return false;
		}
		final Class<?> r = getClass(s);
		return r != null && o.getClass().isAssignableFrom(r);
	}

	private FieldConfig getFieldConfig() {
		final StackTraceElement e = getCallingAPI();
		final String c = e.getClassName().replace('.', '/'), m = e.getMethodName(), k = c + '.' + m;
		return configs.get(k);
	}

	public static List<java.lang.reflect.Field> getFields(final Class<?> cls) {
		final List<java.lang.reflect.Field> f = new ArrayList<java.lang.reflect.Field>();
		Collections.addAll(f, cls.getDeclaredFields());

		final Class<?> p = cls.getSuperclass();
		if (p != null && !p.equals(Object.class)) {
			f.addAll(getFields(p));
		}

		return f;
	}
}
