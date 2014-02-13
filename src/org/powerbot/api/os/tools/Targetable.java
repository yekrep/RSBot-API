package org.powerbot.api.os.tools;

import java.awt.Point;

public interface Targetable {
	public Point getNextPoint();

	public boolean contains(final Point point);
}
