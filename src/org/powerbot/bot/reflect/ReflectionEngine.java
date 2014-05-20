package org.powerbot.bot.reflect;

import java.util.Map;

public class ReflectionEngine {
	private final ClassLoader loader;
	private final Map<String, Map<String, Field>> fields;

	public ReflectionEngine(final ClassLoader loader, final ReflectionSpec spec) {
		this.loader = loader;
		this.fields = spec.fields;
	}

	public ReflectionEngine(final ClassLoader loader, final Map<String, Map<String, Field>> fields) {
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
		return accessBool(accessor, false);
	}

	public boolean accessBool(final ContextAccessor accessor, final boolean d) {
		final Boolean b = access(accessor, Boolean.class);
		return b != null ? b : d;
	}

	public int accessInt(final ContextAccessor accessor) {
		return accessInt(accessor, -1);
	}

	public int accessInt(final ContextAccessor accessor, final int d) {
		final Field f = getField();
		if (f == null) {
			return d;
		}
		final Integer i = access(accessor, Integer.class);
		return i != null ? f.type == 1 ? i * (int) f.multiplier : i : d;
	}

	public long accessLong(final ContextAccessor accessor) {
		return accessLong(accessor, -1l);
	}

	public long accessLong(final ContextAccessor accessor, final long d) {
		final Field f = getField();
		if (f == null) {
			return d;
		}
		final Long i = access(accessor, Long.class);
		return i != null ? f.type == 2 ? i * f.multiplier : i : d;
	}

	public float accessFloat(final ContextAccessor accessor) {
		return accessFloat(accessor, -1f);
	}

	public float accessFloat(final ContextAccessor accessor, final float d) {
		final Float i = access(accessor, Float.class);
		return i != null ? i : d;
	}

	public byte accessByte(final ContextAccessor accessor) {
		return accessByte(accessor, (byte) -1);
	}

	public byte accessByte(final ContextAccessor accessor, final byte d) {
		final Byte i = access(accessor, Byte.class);
		return i != null ? i : d;
	}

	public short accessShort(final ContextAccessor accessor) {
		return accessShort(accessor, (short) -1);
	}

	public short accessShort(final ContextAccessor accessor, final short d) {
		final Short i = access(accessor, Short.class);
		return i != null ? i : d;
	}

	public Object access(final ContextAccessor accessor) {
		return access(accessor, Object.class);
	}

	public <T> T access(final ContextAccessor accessor, final Class<T> type) {
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
		return type.cast(o);
	}

	private StackTraceElement getCallingAPI() {
		final String n = ReflectionEngine.class.getName();
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
