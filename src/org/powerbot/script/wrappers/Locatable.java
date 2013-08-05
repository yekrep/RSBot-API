package org.powerbot.script.wrappers;

import java.util.Comparator;

import org.powerbot.script.lang.Filter;

public interface Locatable {
	public Tile getLocation();

	public interface Query<T> {
		public T at(Locatable t);

		public T within(double d);

		public T within(Locatable t, double d);

		public T nearest();

		public T nearest(Locatable t);
	}

	public class Matcher implements Filter<Locatable> {
		private final Locatable target;

		public Matcher(final Locatable target) {
			this.target = target;
		}

		@Override
		public boolean accept(final Locatable l) {
			Tile tile = l != null ? l.getLocation() : null;
			Tile target = this.target.getLocation();
			return tile != null && target != null && target.equals(tile);
		}
	}

	public class WithinRange implements Filter<Locatable> {
		private final Locatable target;
		private final double distance;

		public WithinRange(final Locatable target, final double distance) {
			this.target = target;
			this.distance = distance;
		}

		@Override
		public boolean accept(final Locatable l) {
			Tile tile = l != null ? l.getLocation() : null;
			Tile target = this.target.getLocation();
			return tile != null && target != null && tile.distanceTo(target) <= distance;
		}
	}

	public class NearestTo implements Comparator<Locatable> {
		private final Locatable target;

		public NearestTo(final Locatable target) {
			this.target = target;
		}

		@Override
		public int compare(final Locatable o1, final Locatable o2) {
			Tile target = this.target.getLocation();
			Tile t1 = o1.getLocation(), t2 = o2.getLocation();
			if (target == null || t1 == null || t2 == null) {
				return Integer.MAX_VALUE;
			}
			double d1 = t1.distanceTo(target), d2 = t2.distanceTo(target);
			return Double.compare(d1, d2);
		}
	}
}
