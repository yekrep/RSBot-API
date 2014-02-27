package org.powerbot.script.tools;

import org.powerbot.script.lang.Filter;

public interface Nameable {
	public String getName();

	public interface Query<T> {
		public T name(String... names);

		public T name(Nameable... names);
	}

	public class Matcher implements Filter<Nameable> {
		private final String[] names;

		public Matcher(final String... names) {
			this.names = names;
		}

		public Matcher(final Nameable... names) {
			this.names = new String[names.length];
			for (int i = 0; i < names.length; i++) {
				this.names[i] = names[i].getName();
			}
		}

		@Override
		public boolean accept(final Nameable i) {
			final String n = i.getName();
			if (n == null) {
				return false;
			}
			for (final String name : names) {
				if (name != null && name.equalsIgnoreCase(n)) {
					return true;
				}
			}
			return false;
		}
	}
}
