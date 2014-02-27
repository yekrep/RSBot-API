package org.powerbot.script.tools;

import java.awt.Point;

public interface Targetable {
	public Point getInteractPoint();

	public Point getNextPoint();

	public Point getCenterPoint();

	public boolean contains(Point point);
}
