package org.powerbot.script.util;

import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Filters {
	private static <T> T[] flatten(T[][] paramArrayOfT) {
		final List<T> list = new ArrayList<>();
		for (T[] arr : paramArrayOfT) Collections.addAll(list, arr);
		return list.toArray((T[]) new Object[list.size()]);
	}

	public static <T> Filter<T> and(final Filter<T> first, final Filter<T> second, final Filter<T>[] rest) {
		return new Filter<T>() {
			public boolean accept(T paramT) {
				Filter<T>[] filters = Filters.flatten(new Filter[][]{{first, second}, rest});
				for (Filter<T> filter : filters) if (!filter.accept(paramT)) return false;
				return true;
			}
		};
	}

	public static <T> Filter<T> or(final Filter<T> first, final Filter<T> second, final Filter<T>[] rest) {
		return new Filter<T>() {
			@Override
			public boolean accept(T t) {
				Filter<T>[] filters = Filters.flatten(new Filter[][]{{first, second}, rest});
				for (Filter<T> filter : filters) if (filter.accept(t)) return true;
				return false;
			}
		};
	}

	public static <T> Filter<T> not(final Filter<T> filter) {
		return new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return !filter.accept(t);
			}
		};
	}

	public static <T> T[] filter(T[] arr, Filter<T> filter) {
		arr = arr.clone();
		int d = 0;
		for (int i = 0; i < arr.length; i++) if (filter.accept(arr[i])) arr[d++] = arr[i];
		return Arrays.copyOf(arr, d);
	}

	public static <T> boolean accept(T[] arr, Filter<T> filter) {
		for (T a : arr) if (filter.accept(a)) return true;
		return false;
	}

	public static <T> Filter<T> accept(final T obj) {
		return new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return obj != null && obj.equals(t);
			}
		};
	}

	public static <T extends Identifiable> T[] id(T[] arr, final int... ids) {
		return filter(arr, new Filter<T>() {
			@Override
			public boolean accept(T t) {
				if (t == null) return false;
				int _id = t.getId();
				for (int id : ids) if (id == _id) return true;
				return false;
			}
		});
	}

	public static <T extends Locatable> T[] range(T[] arr, Locatable local, final double range) {
		if (local == null) return Arrays.copyOf(arr, 0);

		final Tile pos = local.getLocation();
		if (pos == null) return Arrays.copyOf(arr, 0);
		return filter(arr, new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return distance(pos, t) < range;
			}
		});
	}

	public static <T extends Locatable> T[] at(T[] arr, Locatable loc) {
		final Tile pos = loc.getLocation();
		if (pos == null) return Arrays.copyOf(arr, 0);
		return filter(arr, new Filter<T>() {
			@Override
			public boolean accept(T t) {
				Tile tile = t.getLocation();
				return tile != null && tile.equals(pos);
			}
		});
	}

	public static <T extends Locatable> T nearest(T[] arr, Locatable local) {
		T nearest = null;
		double dist = Double.MAX_VALUE;

		if (local == null) return null;
		Tile pos = local.getLocation();
		if (pos == null) return null;
		for (T t : arr) {
			double d;
			if ((d = distance(pos, t)) < dist) {
				nearest = t;
				dist = d;
			}
		}

		return nearest;
	}

	private static double distance(final int x1, final int y1, final int x2, final int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	private static double distance(final Locatable a, final Locatable b) {
		final Tile tA = a != null ? a.getLocation() : null, tB = b != null ? b.getLocation() : null;
		if (tA == null || tB == null) return Double.MAX_VALUE;
		return distance(tA.x, tA.y, tB.x, tB.y);
	}
}
