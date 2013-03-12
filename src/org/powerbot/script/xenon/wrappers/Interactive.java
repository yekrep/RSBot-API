package org.powerbot.script.xenon.wrappers;

import java.awt.Point;

import org.powerbot.script.xenon.Mouse;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Random;

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
						try {
							Thread.sleep(Random.nextInt(0, 50));
						} catch (final InterruptedException ignored) {
						}
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
