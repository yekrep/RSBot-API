package org.powerbot.script.internal.input;

import java.awt.Point;

import org.powerbot.golem.math.Vector3;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.script.xenon.wrappers.Targetable;

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
