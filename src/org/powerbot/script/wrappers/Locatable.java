package org.powerbot.script.wrappers;

import java.util.Comparator;

import org.powerbot.script.lang.AbstractQuery;

public interface Locatable {
	public Tile getLocation();

	public class Matcher implements AbstractQuery.Filter<Locatable> {
		private final Locatable target;

		public Matcher(final Locatable target) {
			this.target = target;
		}

		@Override
		public boolean accept(final Locatable l) {
			return l.getLocation().equals(target);
		}
	}

	public class WithinRange implements AbstractQuery.Filter<Locatable> {
		private final Locatable target;
		private final double distance;

		public WithinRange(final Locatable target, final double distance) {
			this.target = target;
			this.distance = distance;
		}

		@Override
		public boolean accept(final Locatable l) {
			return l.getLocation().distanceTo(target.getLocation()) <= distance;
		}
	}

	public class NearestTo implements Comparator<Locatable> {
		private final Locatable target;

		public NearestTo(final Locatable target) {
			this.target = target;
		}

		@Override
		public int compare(final Locatable o1, final Locatable o2) {
			final Tile t = target.getLocation();
			final double d1 = o1.getLocation().distanceTo(t), d2 = o2.getLocation().distanceTo(t);
			return Double.compare(d1, d2);
		}
	}
}
