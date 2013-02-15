package org.powerbot.core.script.internal.input;

import java.awt.Point;

import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;

class Target {
	Targetable targetable;
	Filter<Point> filter;
	MouseCallback callback;
	Point interactPoint;
	int steps;

	public Target(final Targetable targetable, final Filter<Point> filter, final MouseCallback callback) {
		this.targetable = targetable;
		this.filter = filter;
		this.callback = callback;
		this.interactPoint = null;
		this.steps = 0;
	}
}
