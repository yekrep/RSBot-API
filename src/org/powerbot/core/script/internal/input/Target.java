package org.powerbot.core.script.internal.input;

import java.awt.Point;

import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;
import org.powerbot.math.Vector3;

class Target {
	Targetable targetable;
	Filter<Point> filter;
	MouseCallback callback;
	Vector3 curr;
	Vector3 dest;
	int steps;

	public Target(final Targetable targetable, final Filter<Point> filter, final MouseCallback callback) {
		this.targetable = targetable;
		this.filter = filter;
		this.callback = callback;
		this.curr = null;
		this.dest = null;
		this.steps = 0;
	}
}
