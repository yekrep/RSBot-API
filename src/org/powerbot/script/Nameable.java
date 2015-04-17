package org.powerbot.script;

import java.util.regex.Pattern;

public interface Nameable {
	String name();

	interface Query<T> {
		T name(String... names);

		T name(Pattern... names);

		T name(Nameable... names);
	}

	class Matcher implements Filter<Nameable> {
		private final String[] str;
		private final Pattern[] regex;

		public Matcher(final String... names) {
			str = names;
			regex = null;
		}

		public Matcher(final Nameable... names) {
			regex = null;
			this.str = new String[names.length];
			for (int i = 0; i < names.length; i++) {
				this.str[i] = names[i].name();
			}
		}

		public Matcher(final Pattern... names) {
			regex = names;
			str = null;
		}

		@Override
		public boolean accept(final Nameable i) {
			final String n = i.name();
			if (n == null) {
				return false;
			}
			for (final Object name : regex == null ? str : regex) {
				if (name != null && (name instanceof Pattern ? ((Pattern) name).matcher(n).matches() : ((String) name).equalsIgnoreCase(n))) {
					return true;
				}
			}
			return false;
		}
	}
}
