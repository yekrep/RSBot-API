package org.powerbot.core.script.internal.input;

import java.awt.Point;

import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;
import org.powerbot.math.Vector3;

public abstract class MouseTarget implements MouseCallback {
	public static final Filter<Point> DUMMY = new Filter<Point>() {
		@Override
		public boolean accept(final Point point) {
			return true;
		}
	};
	public boolean failed;
	Targetable targetable;
	public Filter<Point> filter;
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
