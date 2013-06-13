package org.powerbot.script.wrappers;

import org.powerbot.script.util.Filter;

public interface Identifiable {
	public int getId();

	public class Matcher implements Filter<Identifiable> {
		private final int[] ids;

		public Matcher(final int... ids) {
			this.ids = ids;
		}

		public Matcher(final Identifiable... ids) {
			this.ids = new int[ids.length];
			for (int i = 0; i < ids.length; i++) {
				this.ids[i] = ids[i].getId();
			}
		}

		@Override
		public boolean accept(final Identifiable i) {
			final int x = i.getId();
			for (int id : ids) {
				if (id == x) {
					return true;
				}
			}
			return false;
		}
	}
}
