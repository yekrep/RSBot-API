package org.powerbot.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Reflector {
	private final ClassLoader loader;
	private final Map<String, String> interfaces;
	private final Map<String, Map<String, Field>> fields;

	public Reflector(final ClassLoader loader, final TransformSpec spec) {
		this(loader, spec.interfaces, spec.fields);
	}

	public Reflector(final ClassLoader loader, final Map<String, String> interfaces, final Map<String, Map<String, Field>> fields) {
		this.loader = loader;
		this.interfaces = interfaces;
		this.fields = fields;
		System.out.println(fields);
	}

	public static class Field {
		private final String parent, name;
		private final boolean virtual;
		private final byte type;
		private final long multiplier;

		public Field(final String parent, final String name, final boolean virtual, final byte type, final long multiplier) {
			this.parent = parent;
			this.name = name;
			this.virtual = virtual;
			this.type = type;
			this.multiplier = multiplier;
		}

		@Override
		public String toString() {
			return String.format("%s.%s %s %d:%d", parent, name, Boolean.toString(virtual), type, multiplier);
		}
	}

	public boolean accessBool(final ReflectProxy accessor) {
		return access(accessor, false);
	}

	public int accessInt(final ReflectProxy accessor) {
		final Field f = getField();
		if (f == null) {
			return -1;
		}
		final Integer i = access(accessor, Integer.class);
		return i != null ? f.type == 1 ? i * (int) f.multiplier : i : -1;
	}

	public int[] accessInts(final ReflectProxy accessor) {
		return access(accessor, int[].class);
	}

	public long accessLong(final ReflectProxy accessor) {
		final Field f = getField();
		if (f == null) {
			return -1l;
		}
		final Long i = access(accessor, Long.class);
		return i != null ? f.type == 2 ? i * f.multiplier : i : -1l;
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
		final Object obj = accessor.obj.get();
		if (obj == null) {
			return null;
		}
		final Field f = getField();
		Class<?> c2;
		if (f.virtual) {
			c2 = obj.getClass();
		} else {
			final String s = f.parent;
			if (s == null || s.isEmpty()) {
				return null;
			}
			try {
				c2 = loader.loadClass(s);
			} catch (final ClassNotFoundException ignored) {
				return null;
			}
		}
		java.lang.reflect.Field f2 = null;
		if (f.virtual) {
			while (f2 == null && c2 != Object.class) {
				try {
					f2 = c2.getDeclaredField(f.name);
				} catch (final NoSuchFieldException ignored) {
					c2 = c2.getSuperclass();
				}
			}
		} else {
			try {
				f2 = c2.getDeclaredField(f.name);
			} catch (final NoSuchFieldException ignored) {
			}
		}
		if (f2 == null) {
			return null;
		}
		final boolean a2 = f2.isAccessible();
		f2.setAccessible(true);
		Object o = null;
		try {
			o = f2.get(f.virtual ? obj : null);
		} catch (final IllegalAccessException ignored) {
		}
		f2.setAccessible(a2);
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

	private Field getField() {
		final StackTraceElement e = getCallingAPI();
		final String c = interfaces.get(e.getClassName().replace('.', '/')), m = e.getMethodName();
		if (c == null) {
			return null;
		}
		final Map<String, Field> map = fields.get(c);
		if (map == null || !map.containsKey(m)) {
			return null;
		}
		return map.get(m);
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
