package org.powerbot.script;

import java.util.regex.Pattern;

/**
 * Actionable
 * An entity which holds an array of actions.
 */
public interface Actionable extends Interactive {
	/**
	 * The current actions for the entity.
	 *
	 * @return the current entity actions
	 */
	String[] actions();

	/**
	 * Query
	 * A base for queries that make use of {@link Actionable} entities.
	 *
	 * @param <T> the type of query to return for chaining
	 */
	interface Query<T> {
		/**
		 * Selects the entities which have one of the specified actions into the query cache.
		 *
		 * @param actions the valid actions
		 * @return {@code this} for the purpose of method chaining
		 */
		T action(String... actions);

		/**
		 * Selects the entities which have any action which matches one of the specified action patterns into the query cache.
		 *
		 * @param actions the valid patterns to check RegEx against
		 * @return {@code this} for the purpose of method chaining
		 */
		T action(Pattern... actions);
	}

	/**
	 * Matcher
	 */
	class Matcher implements Filter<Actionable> {
		private final String[] str;
		private final Pattern[] regex;

		public Matcher(final String... actions) {
			str = actions;
			regex = null;
		}

		public Matcher(final Pattern... actions) {
			regex = actions;
			str = null;
		}

		@Override
		public boolean accept(final Actionable i) {
			final String[] actions = i.actions();
			if (actions == null) {
				return false;
			}
			final Object[] list = regex == null ? str : regex;
			if (list == null) {
				return false;
			}
			for (final Object action : list) {
				for (final String a : actions) {
					if (action != null && a != null &&
							(action instanceof Pattern ?
									((Pattern) action).matcher(a).matches() :
									((String) action).equalsIgnoreCase(a)
							)) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
