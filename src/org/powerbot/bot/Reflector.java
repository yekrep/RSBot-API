package org.powerbot.bot;

import java.util.Map;

public class Reflector {
	private final ClassLoader loader;
	private final Map<String, Map<String, Field>> fields;

	public Reflector(final ClassLoader loader, final TransformSpec spec) {
		this.loader = loader;
		this.fields = spec.fields;
	}

	public Reflector(final ClassLoader loader, final Map<String, Map<String, Field>> fields) {
		this.loader = loader;
		this.fields = fields;
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
	}

	public boolean accessBool(final ContextAccessor accessor) {
		return access(accessor, false);
	}

	public int accessInt(final ContextAccessor accessor) {
		return access(accessor, -1);
	}

	public int[] accessInts(final ContextAccessor accessor) {
		return access(accessor, int[].class);
	}

	public long accessLong(final ContextAccessor accessor) {
		return access(accessor, -1L);
	}

	public float accessFloat(final ContextAccessor accessor) {
		return access(accessor, -1f);
	}

	public byte accessByte(final ContextAccessor accessor) {
		return access(accessor, (byte) -1);
	}

	public short accessShort(final ContextAccessor accessor) {
		return access(accessor, (short) -1);
	}

	public String accessString(final ContextAccessor accessor) {
		return access(accessor, String.class);
	}

	public Object access(final ContextAccessor accessor) {
		return access(accessor, null);
	}

	public   <T> T access(final ContextAccessor accessor, final Class<T> t) {
		if (accessor.root == null) {
			return null;
		}
		final Field f = getField();
		Class<?> c2;
		if (f.virtual) {
			c2 = accessor.root.getClass();
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
			o = f2.get(f.virtual ? accessor.root : null);
		} catch (final IllegalAccessException ignored) {
		}
		f2.setAccessible(a2);
		return t.cast(o);
	}

	public <T> T access(final ContextAccessor accessor, final T d) {
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
		final String c = e.getClassName(), m = e.getMethodName();
		final Map<String, Field> map = fields.get(c);
		if (map == null || !map.containsKey(m)) {
			return null;
		}
		return map.get(m);
	}
}
