package org.powerbot.script;

/**
 * Textable
 * An entity which contains a test description.
 */
public interface Textable {
	/**
	 * The text description of the entity.
	 *
	 * @return the entity's text description
	 */
	String text();

	/**
	 * Query
	 *
	 * @param <T>
	 */
	interface Query<T> {
		T text(String... texts);
	}

	/**
	 * Matcher
	 */
	class Matcher implements Filter<Textable> {
		private final String[] texts;

		public Matcher(final String... texts) {
			this.texts = new String[texts.length];
			for (int i = 0; i < texts.length; i++) {
				this.texts[i] = texts[i].toLowerCase();
			}
		}

		@Override
		public boolean accept(final Textable t) {
			String str = t.text();
			if (str == null) {
				return false;
			}
			str = str.toLowerCase();
			for (final String text : texts) {
				if (text != null && text.contains(str)) {
					return true;
				}
			}
			return false;
		}
	}
}
