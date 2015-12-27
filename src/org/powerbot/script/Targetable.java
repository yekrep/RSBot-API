package org.powerbot.script;

import java.awt.Point;

public interface Targetable {
	Point nextPoint();

	boolean contains(final Point point);
}
