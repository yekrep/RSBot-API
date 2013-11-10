package org.powerbot.script.methods;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.powerbot.client.Client;
import org.powerbot.script.internal.MouseHandler;
import org.powerbot.script.internal.MouseTarget;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.wrappers.Targetable;
import org.powerbot.util.math.HardwareSimulator;

public class Mouse extends MethodProvider {
	public Mouse(MethodContext factory) {
		super(factory);
	}

	/**
	 * Returns the current position of the mouse.
	 *
	 * @return position of the mouse
	 */
	public Point getLocation() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
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
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
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
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
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
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPressed();
	}

	/**
	 * Returns if the mouse is currently present.
	 *
	 * @return <tt>true</tt> if the mouse is present; otherwise <tt>false</tt>
	 */
	public boolean isPresent() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPresent();
	}

	/**
	 * Scrolls by one unit either down or up.
	 *
	 * @param down <tt>true</tt> to scroll down; otherwise <tt>false</tt> to scroll up
	 * @return <tt>true</tt> if scrolled; otherwise <tt>false</tt>
	 */
	public boolean scroll(boolean down) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		handler.scroll(down);
		return true;
	}

	public boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public boolean hop(final int x, final int y) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		handler.move(x, y);
		return true;
	}

	public boolean click(boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(int button) {
		final Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return false;
		}

		return click(mouse.getX(), mouse.getY(), button);
	}

	public boolean click(Point p, boolean left) {
		return click(p.x, p.y, left);
	}

	public boolean click(int x, int y, boolean left) {
		return click(x, y, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(int x, int y, int button) {
		MouseHandler handler = getMouseHandler();
		if (handler != null && move(x, y)) {
			handler.click(x, y, button);
			return true;
		}
		return false;
	}

	public boolean click(final Targetable target, final boolean left) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, MouseTarget.DUMMY) {
			@Override
			public void execute(final MouseHandler handler) {
				try {
					Thread.sleep(HardwareSimulator.getDelayFactor());
				} catch (InterruptedException ignored) {
				}

				if (filter.accept(handler.getLocation())) {
					click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
					handler.complete(this);
				}
			}
		});
		return !t.failed;
	}

	public boolean drag(final Point p1, final Point p2, final boolean left) {
		return drag(p1, p2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean drag(final Point p1, final Point p2, final int button) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		Point loc = handler.getLocation();
		if (loc.equals(p1) || move(p1)) {
			try {
				Thread.sleep(handler.simulator.getPressDuration());
			} catch (InterruptedException ignored) {
			}
			handler.press(p1.x, p1.y, button);
			try {
				Thread.sleep(handler.simulator.getPressDuration());
			} catch (InterruptedException ignored) {
			}
			if (move(p2)) {
				try {
					Thread.sleep(handler.simulator.getPressDuration());
				} catch (InterruptedException ignored) {
				}
				handler.release(p2.x, p2.y, button);
				try {
					Thread.sleep(handler.simulator.getPressDuration());
				} catch (InterruptedException ignored) {
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
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, filter) {
			@Override
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
		return !t.failed;
	}

	public boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public boolean move(final Point p) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

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
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
		return !t.failed;
	}

	public boolean isReady() {
		MouseHandler handler = getMouseHandler();
		return handler != null && handler.getSource() != null;
	}

	private MouseHandler getMouseHandler() {
		return ctx.getBot().getMouseHandler();
	}
}
