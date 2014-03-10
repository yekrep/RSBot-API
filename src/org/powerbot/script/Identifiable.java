package org.powerbot.script;

public interface Identifiable {
	public int id();

	public interface Query<T> {
		public T id(int... ids);

		public T id(int[]... ids);

		public T id(Identifiable... ids);
	}

	public class Matcher implements Filter<Identifiable> {
		private final int[] ids;

		public Matcher(final int... ids) {
			this.ids = ids;
		}

		public Matcher(final Identifiable... ids) {
			this.ids = new int[ids.length];
			for (int i = 0; i < ids.length; i++) {
				this.ids[i] = ids[i].id();
			}
		}

		@Override
		public boolean accept(final Identifiable i) {
			final int x = i != null ? i.id() : -1;
			if (x == -1) {
				return false;
			}
			for (final int id : ids) {
				if (id == x) {
					return true;
				}
			}
			return false;
		}
	}
}
