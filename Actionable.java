package org.powerbot.script;

import java.util.regex.Pattern;

public interface Actionable extends Interactive {
	String[] actions();

	interface Query<T> {
		T action(String... actions);

		T action(Pattern... actions);
	}

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
