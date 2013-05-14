package org.powerbot.script.xenon.wrappers;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Menu;
import org.powerbot.script.xenon.Mouse;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Filter;

public abstract class Interactive implements Targetable, Validatable {
	private static final int ATTEMPTS = 5;

	public boolean isOnScreen() {
		return Game.isPointOnScreen(getInteractPoint());
	}

	public boolean hover() {
		return Mouse.move(this);
	}

	public boolean click() {
		return click(true);
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
					if (contains(point) && Menu.indexOf(action, option) != -1) {
						Delay.sleep(0, 50);
						return contains(point) && Menu.indexOf(action, option) != -1;
					}
					return false;
				}
			})) {
				continue;
			}

			if (Menu.click(action, option)) return true;
			Menu.close();
		}
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public void draw(final Graphics graphics) {
	}
}
