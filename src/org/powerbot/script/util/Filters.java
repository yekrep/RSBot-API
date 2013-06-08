package org.powerbot.script.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.powerbot.script.wrappers.Identifiable;

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
}
