package org.powerbot.script;

import java.util.Comparator;

public interface Locatable {
	Tile tile();

	interface Query<T> {
		T at(Locatable t);

		T within(double d);

		T within(Locatable t, double d);

		T within(Area area);

		T nearest();

		T nearest(Locatable t);
	}

	class Matcher implements Filter<Locatable> {
		private final Locatable target;

		public Matcher(final Locatable target) {
			this.target = target;
		}

		@Override
		public boolean accept(final Locatable l) {
			final Tile tile = l != null ? l.tile() : null;
			final Tile target = this.target.tile();
			return tile != null && target != null && target.equals(tile);
		}
	}

	class WithinRange implements Filter<Locatable> {
		private final Locatable target;
		private final double distance;

		public WithinRange(final Locatable target, final double distance) {
			this.target = target;
			this.distance = distance;
		}

		@Override
		public boolean accept(final Locatable l) {
			final Tile tile = l != null ? l.tile() : null;
			final Tile target = this.target.tile();
			return tile != null && target != null && tile.distanceTo(target) <= distance;
		}
	}

	class WithinArea implements Filter<Locatable> {
		private final Area area;

		public WithinArea(final Area area) {
			this.area = area;
		}

		@Override
		public boolean accept(final Locatable l) {
			final Tile tile = l != null ? l.tile() : null;
			return tile != null && area.contains(tile);
		}
	}

	class NearestTo implements Comparator<Locatable> {
		private final Locatable target;

		public NearestTo(final Locatable target) {
			this.target = target;
		}

		@Override
		public int compare(final Locatable o1, final Locatable o2) {
			final Tile target = this.target.tile();
			final Tile t1 = o1.tile();
			final Tile t2 = o2.tile();
			if (target == null || t1 == null || t2 == null) {
				return Integer.MAX_VALUE;
			}
			final double d1 = t1.distanceTo(target);
			final double d2 = t2.distanceTo(target);
			return Double.compare(d1, d2);
		}
	}
}
