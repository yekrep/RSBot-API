package org.powerbot.os.api.wrappers;

import java.awt.Point;

public interface Targetable {
	public Point getNextPoint();

	public Point getCenterPoint();

	public boolean contains(final Point point);
}
