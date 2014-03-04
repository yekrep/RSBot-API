package org.powerbot.script.rs3.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.script.MouseSimulator;
import org.powerbot.bot.script.MouseTarget;
import org.powerbot.script.lang.Filter;
import org.powerbot.util.math.HardwareSimulator;

public class Mouse extends ClientAccessor {
	final MouseSimulator handler;

	public Mouse(final ClientContext ctx) {
		super(ctx);
		handler = new MouseSimulator(ctx);
	}

	/**
	 * Returns the current position of the mouse.
	 *
	 * @return position of the mouse
	 */
	public Point getLocation() {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getLocation();
	}

	/**
	 * Returns the last press location.
	 *
	 * @return the press location
	 */
	public Point getPressLocation() {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getPressLocation();
	}

	/**
	 * Returns the last press time.
	 *
	 * @return the press time
	 */
	public long getPressTime() {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return -1;
		}
		return mouse.getPressTime();
	}

	/**
	 * Returns if the mouse is currently pressed.
	 *
	 * @return <tt>true</tt> if the mouse is pressed; otherwise <tt>false</tt>
	 */
	public boolean isPressed() {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPressed();
	}

	/**
	 * Returns if the mouse is currently present.
	 *
	 * @return <tt>true</tt> if the mouse is present; otherwise <tt>false</tt>
	 */
	public boolean isPresent() {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPresent();
	}

	/**
	 * Scrolls by one unit either down or up.
	 *
	 * @param down <tt>true</tt> to scroll down; otherwise <tt>false</tt> to scroll up
	 * @return <tt>true</tt> if scrolled; otherwise <tt>false</tt>
	 */
	public boolean scroll(final boolean down) {
		handler.scroll(down);
		return true;
	}

	public boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public boolean hop(final int x, final int y) {
		handler.move(x, y);
		return true;
	}

	public boolean click(final boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(final int button) {
		final Client client = ctx.getClient();
		final org.powerbot.bot.rs3.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && click(mouse.getX(), mouse.getY(), button);

	}

	public boolean click(final Point p, final boolean left) {
		return click(p.x, p.y, left);
	}

	public boolean click(final int x, final int y, final boolean left) {
		return click(x, y, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(final int x, final int y, final int button) {
		if (move(x, y)) {
			handler.click(x, y, button);
			return true;
		}
		return false;
	}

	public boolean click(final Targetable target, final boolean left) {
		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, MouseTarget.DUMMY) {
			@Override
			public boolean execute(final MouseSimulator handler) {
				try {
					Thread.sleep(HardwareSimulator.getDelayFactor());
				} catch (final InterruptedException ignored) {
				}

				return filter.accept(handler.getLocation()) && click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
			}
		});
		return !t.failed;
	}

	public boolean drag(final Point p1, final Point p2, final boolean left) {
		return drag(p1, p2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean drag(final Point p1, final Point p2, final int button) {
		final Point loc = handler.getLocation();
		if (loc.equals(p1) || move(p1)) {
			try {
				Thread.sleep(handler.simulator.getPressDuration());
			} catch (final InterruptedException ignored) {
			}
			handler.press(p1.x, p1.y, button);
			try {
				Thread.sleep(handler.simulator.getPressDuration());
			} catch (final InterruptedException ignored) {
			}
			if (move(p2)) {
				try {
					Thread.sleep(handler.simulator.getPressDuration());
				} catch (final InterruptedException ignored) {
				}
				handler.release(p2.x, p2.y, button);
				try {
					Thread.sleep(handler.simulator.getPressDuration());
				} catch (final InterruptedException ignored) {
				}
				return true;
			}
		}
		return false;
	}

	public boolean drag(final int x1, final int y1, final int x2, final int y2, final boolean left) {
		return drag(x1, y1, x2, y2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean drag(final int x1, final int y1, final int x2, final int y2, final int button) {
		return drag(new Point(x1, y1), new Point(x2, y2), button);
	}

	public boolean drag(final int x, final int y, final boolean left) {
		return drag(getLocation(), new Point(x, y), left);
	}

	public boolean drag(final Point p, final boolean left) {
		return drag(getLocation(), p, left);
	}

	public boolean drag(final int x, final int y, final int button) {
		return drag(getLocation(), new Point(x, y), button);
	}

	public boolean drag(final Point p, final int button) {
		return drag(getLocation(), p, button);
	}

	public boolean move(final Targetable target) {
		return move(target, MouseTarget.DUMMY);
	}

	public boolean move(final Targetable target, final Filter<Point> filter) {
		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, filter) {
			@Override
			public boolean execute(final MouseSimulator handler) {
				return true;
			}
		});
		return !t.failed;
	}

	public boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public boolean move(final Point p) {
		final MouseTarget t;
		final Targetable targetable = new Targetable() {
			@Override
			public Point getInteractPoint() {
				return p;
			}

			@Override
			public Point getNextPoint() {
				return p;
			}

			@Override
			public Point getCenterPoint() {
				return p;
			}

			@Override
			public boolean contains(final Point point) {
				return point.equals(p);
			}
		};

		handler.handle(t = new MouseTarget(targetable, MouseTarget.DUMMY) {
			@Override
			public boolean execute(final MouseSimulator handler) {
				return true;
			}
		});
		return !t.failed;
	}
}
