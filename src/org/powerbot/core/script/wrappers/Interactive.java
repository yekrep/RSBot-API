package org.powerbot.core.script.wrappers;

import java.awt.Point;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.methods.Mouse;
import org.powerbot.core.script.util.Filter;
import org.powerbot.game.api.methods.node.Menu;

public abstract class Interactive implements Targetable {
	private static final int ATTEMPTS = 5;

	public boolean isOnScreen() {
		final Point p = getInteractPoint();
		return p.x != -1 && p.y != -1;
	}

	public boolean hover() {
		return Mouse.move(this);
	}

	public boolean click(final boolean left) {
		return Mouse.click(this, left);
	}

	public boolean interact(final String action) {
		return interact(action, null);
	}

	public boolean interact(final String action, final String option) {
		int a = 0;
		while (a++ < ATTEMPTS) {
			if (!Mouse.move(this, new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					if (contains(point) && Menu.contains(action, option)) {
						Task.sleep(5, 50);
						return contains(point) && Menu.contains(action, option);
					}
					return false;
				}
			})) {
				continue;
			}

			if (Menu.select(action, option)) return true;
			Menu.close();
		}
		return false;
	}
}
