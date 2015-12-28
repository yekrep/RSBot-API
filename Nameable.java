package org.powerbot.script;

import java.util.regex.Pattern;

/**
 * Nameable
 * An entity which has a name.
 */
public interface Nameable {
	/**
	 * The name of the entity.
	 *
	 * @return the entity's name
	 */
	String name();

	/**
	 * Query
	 * A base for queries that make use of {@link Nameable} entities.
	 *
	 * @param <T> the type of query to return for chaining
	 */
	interface Query<T> {
		/**
		 * Selects the entities which have a name that matches any of the specified names into the query cache.
		 *
		 * @param names the valid names
		 * @return {@code this} for the purpose of method chaining
		 */
		T name(String... names);

		/**
		 * Selects the entities which have a name that matches one of the specified action patterns into the query cache.
		 *
		 * @param names the valid patterns to check RegEx against
		 * @return {@code this} for the purpose of method chaining
		 */
		T name(Pattern... names);

		/**
		 * Selects the entities which have a name that matches any of the specified nameables names into the query cache.
		 *
		 * @param names the valid nameables to check against
		 * @return {@code this} for the purpose of method chaining
		 */
		T name(Nameable... names);
	}

	/**
	 * Matcher
	 */
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
