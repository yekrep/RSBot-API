package org.powerbot.bot;

import java.lang.reflect.Modifier;
import java.util.*;

public class Reflector {
	private final ClassLoader loader;
	private final Map<String, String> interfaces;
	private final Map<String, FieldConfig> configs;
	private final Map<String, Long> constants;
	private final Map<String, Class<?>> cache1;
	private final Map<FieldConfig, java.lang.reflect.Field> cache2;

	public Reflector(final ClassLoader loader, final AbstractReflectorSpec spec) {
		this(loader, spec.getInterfaces(), spec.getConfigs(), spec.getConstants());
	}

	public Reflector(final ClassLoader loader, final Map<String, String> interfaces, final Map<String, FieldConfig> configs, final Map<String, Long> constants) {
		this.loader = loader;
		this.interfaces = interfaces;
		this.configs = configs;
		this.constants = constants;
		cache1 = new HashMap<>();
		cache2 = new HashMap<>();
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

		@Override
		public String toString() {
			return String.format("%s[parent=%s;name=%s;type=%s;mult=%d;]", "FieldConfig", parent, name, type, multiplier);
		}
	}

	public static class FieldCache {
		private FieldConfig c;

		public FieldCache() {
			c = null;
		}

		@Override
		public String toString() {
			return c != null ? c.toString() : "null";
		}
	}

	public long getConstant(final String key) {
		final Long l = constants.get(key);
		return l != null ? l : -1;
	}

	public boolean accessBool(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, false);
	}

	public int accessInt(final ReflectProxy accessor, final FieldCache c) {
		final FieldConfig f = c.c != null ? c.c : (c.c = getFieldConfig());
		if (f == null) {
			return -1;
		}
		final Integer i = access(accessor, f, Integer.class);
		return i != null ? i * (int) f.multiplier : -1;
	}

	public int[] accessInts(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, int[].class);
	}

	public long accessLong(final ReflectProxy accessor, final FieldCache c) {
		final FieldConfig f = c.c != null ? c.c : (c.c = getFieldConfig());
		if (f == null) {
			return -1L;
		}
		final Long j = access(accessor, f, Long.class);
		return j != null ? j * f.multiplier : -1L;
	}

	public float accessFloat(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, -1f);
	}

	public double accessDouble(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, -1d);
	}

	public byte accessByte(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, (byte) -1);
	}

	public short accessShort(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, (short) -1);
	}

	public String accessString(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, String.class);
	}

	public Object access(final ReflectProxy accessor, final FieldCache c) {
		return access(accessor, c, Object.class);
	}

	public <T> T access(final ReflectProxy accessor, final FieldCache c, final Class<T> t) {
		return access(accessor, c.c != null ? c.c : (c.c = getFieldConfig()), t);
	}

	private <T> T access(final ReflectProxy accessor, final FieldConfig r, final Class<T> t) {
		final Object p = accessor.obj.get();
		if (r == null) {
			return null;
		}

		final java.lang.reflect.Field f;
		if (cache2.containsKey(r)) {
			f = cache2.get(r);
			if (f == null) {
				throw new RuntimeException("Missing: " + r.toString());
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
			final boolean s = (f.getModifiers() & Modifier.STATIC) != 0;
			if (s) {
				o = f.get(null);
			} else {
				o = p != null ? f.get(p) : null;
			}
		} catch (final IllegalArgumentException | IllegalAccessException ignored) {
			return null;
		}
		return o != null ? t.cast(o) : null;
	}

	private <T> T access(final ReflectProxy accessor, final FieldCache c, final T d) {
		@SuppressWarnings("unchecked") final T v = (T) access(accessor, c, d.getClass());
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

	private String getGroupClass(final String g) {
		final String g2 = g.replace('.', '/');
		for (final Map.Entry<String, String> e : interfaces.entrySet()) {
			if (e.getValue().equals(g2)) {
				return e.getKey();
			}
		}
		return null;
	}

	private Class<?> getClass(final String s) {
		final Class<?> c;//TODO
		if (cache1.containsKey(s)) {
			c = cache1.get(s);
		} else {
			try {
				cache1.put(s, c = loader.loadClass(s));
			} catch (final InternalError | ClassNotFoundException ignored) {
				cache1.put(s, null);
				return null;
			}
		}
		return c;
	}

	public boolean isTypeOf(final Object o, final Class<? extends ReflectProxy> c) {
		try {
			final String s = getGroupClass(c.getName());
			if (o == null || s == null) {
				return false;
			}
			final Class<?> r = getClass(s);
			return r != null && o.getClass().isAssignableFrom(r);
		} catch (final InternalError ignored) {
			return false;
		}
	}

	private FieldConfig getFieldConfig() {
		final StackTraceElement e = getCallingAPI();
		final String c = e.getClassName().replace('.', '/'), m = e.getMethodName(),
				k = (c.endsWith("Client") ? "" : c + '.') + m;
		final FieldConfig r = configs.get(k);
		if (r == null) {
			throw new RuntimeException("Config missing for " + k);
		}
		return r;
	}

	private static List<java.lang.reflect.Field> getFields(final Class<?> cls) {
		final List<java.lang.reflect.Field> f = new ArrayList<>();
		Collections.addAll(f, cls.getDeclaredFields());

		final Class<?> p = cls.getSuperclass();
		if (p != null && !p.equals(Object.class)) {
			f.addAll(getFields(p));
		}

		return f;
	}
}
