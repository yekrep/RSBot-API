package org.powerbot.core.script.internal.input;

import java.awt.Point;

import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;
import org.powerbot.math.Vector3;

public abstract class MouseTarget implements Filter<Point>, MouseCallback {
	public Targetable targetable;
	Vector3 curr;
	Vector3 dest;
	int steps;

	public MouseTarget(final Targetable targetable) {
		this.targetable = targetable;
		this.curr = null;
		this.dest = null;
		this.steps = 0;
	}
}
