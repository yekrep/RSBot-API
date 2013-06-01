package org.powerbot.script.wrappers;

import java.awt.Point;

import org.powerbot.script.methods.World;
import org.powerbot.script.methods.WorldImpl;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Filter;

public abstract class Interactive extends WorldImpl implements Targetable, Validatable {
	private static final int ATTEMPTS = 5;

	public Interactive(World world) {
		super(world);
	}

	public boolean isOnScreen() {
		return world.game.isPointOnScreen(getInteractPoint());
	}

	public boolean hover() {
		return world.mouse.move(this);
	}

	public boolean click() {
		return click(true);
	}

	public boolean click(final boolean left) {
		return world.mouse.click(this, left);
	}

	public boolean interact(final String action) {
		return interact(action, null);
	}

	public boolean interact(final String action, final String option) {
		int a = 0;
		while (a++ < ATTEMPTS) {
			if (!world.mouse.move(this, new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					if (contains(point) && world.menu.indexOf(action, option) != -1) {
						Delay.sleep(0, 80);
						return contains(point) && world.menu.indexOf(action, option) != -1;
					}
					return false;
				}
			})) {
				continue;
			}

			if (world.menu.click(action, option)) return true;
			world.menu.close();
		}
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
