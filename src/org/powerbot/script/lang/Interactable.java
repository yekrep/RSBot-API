package org.powerbot.script.lang;

/**
 * @author Paris
 */
public interface Interactable {
	public boolean isOnScreen();

	public boolean hover();

	public boolean click(boolean left);

	public boolean interact(String action, String option);

	public interface Query<T> {
		public T click(boolean left);

		public T interact(String action, String option);
	}

	public class Clicker<K extends Interactable> implements ChainingIterator<K> {
		private final boolean left;

		public Clicker(final boolean left) {
			this.left = left;
		}

		@Override
		public boolean next(final int index, final K item) {
			return item.click(left);
		}
	}

	public class Interacter<K extends Interactable> implements ChainingIterator<K> {
		private final String action, option;

		public Interacter(final String action, final String option) {
			this.action = action;
			this.option = option;
		}

		@Override
		public boolean next(final int index, final K item) {
			return item.interact(action, option);
		}
	}
}
