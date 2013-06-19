package org.powerbot.script.internal;

import org.powerbot.script.lang.Predicate;
import org.powerbot.util.math.Vector3;
import org.powerbot.script.lang.Targetable;

import java.awt.Point;

public abstract class MouseTarget implements MouseCallback {
	public static final Predicate<Point> DUMMY = new Predicate<Point>() {
		@Override
		public boolean apply(final Point point) {
			return true;
		}
	};
	public boolean failed;
	final Targetable targetable;
	public final Predicate<Point> predicate;
	Vector3 curr;
	Vector3 dest;
	int steps;

	public MouseTarget(final Targetable targetable, final Predicate<Point> predicate) {
		this.targetable = targetable;
		this.predicate = predicate;
		this.curr = null;
		this.dest = null;
		this.steps = 0;
		this.failed = false;
	}
}
