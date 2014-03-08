package org.powerbot.script;

import java.awt.Point;

public interface Targetable {
	public Point nextPoint();

	public boolean contains(final Point point);
}
