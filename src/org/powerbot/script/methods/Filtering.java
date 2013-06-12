package org.powerbot.script.methods;

import java.util.Arrays;

import org.powerbot.script.util.Filter;
import org.powerbot.script.util.Filters;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

/**
 * @author Timer
 */
abstract class Filtering<T> extends ClientLink implements Filterable<T> {
	public Filtering(ClientFactory factory) {
		super(factory);
	}

	@Override
	public Filterable<T> branch() {
		return new FilterClone<>(ctx, list());
	}

	@Override
	public T nearest() {
		return nearest(ctx.players.getLocal());
	}

	@Override
	public T nearest(Locatable loc) {
		T nearest = null;
		double dist = Double.MAX_VALUE;

		if (loc == null) return null;
		Tile pos = loc.getLocation();
		if (pos == null) return null;
		for (T t : list()) {
			if (!(t instanceof Locatable)) break;
			double d;
			if ((d = ctx.movement.distance(pos, (Locatable) t)) < dist) {
				nearest = t;
				dist = d;
			}
		}

		return nearest;
	}

	@Override
	public Filterable<T> filter(Filter<T> filter) {
		return new FilterClone<>(ctx, Filters.filter(list(), filter));
	}

	@Override
	public Filterable<T> range(int range) {
		return range(range, ctx.players.getLocal());
	}

	@Override
	public Filterable<T> range(final int range, Locatable loc) {
		if (loc == null) return new FilterClone<>(ctx, Arrays.copyOf(list(), 0));

		final Tile pos = loc.getLocation();
		if (pos == null) return new FilterClone<>(ctx, Arrays.copyOf(list(), 0));
		return filter(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				if (!(t instanceof Locatable)) return false;
				return ctx.movement.distance(pos, (Locatable) t) < range;
			}
		});
	}

	@Override
	public Filterable<T> at(Locatable loc) {
		final Tile pos = loc.getLocation();
		if (pos == null) return new FilterClone<>(ctx, Arrays.copyOf(list(), 0));
		return filter(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				if (!(t instanceof Locatable)) return false;
				Tile tile = ((Locatable) t).getLocation();
				return tile != null && tile.equals(pos);
			}
		});
	}

	@Override
	public Filterable<T> id(final int... ids) {
		return filter(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				if (!(t instanceof Identifiable)) return false;
				int _id = ((Identifiable) t).getId();
				for (int id : ids) if (id == _id) return true;
				return false;
			}
		});
	}

	@Override
	public boolean accept(Filter<T> filter) {
		return filter(filter).list().length > 0;
	}

	@Override
	public boolean contains(final T obj) {
		return accept(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return t != null && t.equals(obj);
			}
		});
	}
}
