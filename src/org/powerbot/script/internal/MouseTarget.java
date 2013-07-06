package org.powerbot.script.internal;

import java.awt.Point;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.Targetable;
import org.powerbot.util.math.Vector3;

public abstract class MouseTarget implements MouseCallback {
	public static final Filter<Point> DUMMY = new Filter<Point>() {
		@Override
		public boolean accept(final Point point) {
			return true;
		}
	};
	public boolean failed;
	final Targetable targetable;
	public final Filter<Point> filter;
	Vector3 curr;
	Vector3 dest;
	int steps;

	public MouseTarget(final Targetable targetable, final Filter<Point> filter) {
		this.targetable = targetable;
		this.filter = filter;
		this.curr = null;
		this.dest = null;
		this.steps = 0;
		this.failed = false;
	}
}
