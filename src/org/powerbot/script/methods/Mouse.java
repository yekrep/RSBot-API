package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.internal.MouseHandler;
import org.powerbot.script.internal.MouseTarget;
import org.powerbot.script.lang.Targetable;
import org.powerbot.script.util.Delay;
import org.powerbot.script.lang.Predicate;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class Mouse extends MethodProvider {
	public Mouse(MethodContext factory) {
		super(factory);
	}

	public Point getLocation() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getLocation();
	}

	public Point getPressLocation() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getPressLocation();
	}

	public long getPressTime() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return -1;
		}
		return mouse.getPressTime();
	}

	public boolean isPressed() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPressed();
	}

	public boolean isPresent() {
		Client client = ctx.getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPresent();
	}

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

	public boolean click(final int x, final int y, final boolean left) {
		return move(x, y) && click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(final boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(final int button) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		handler.click(button);
		return true;
	}

	public boolean click(final Point p, final boolean left) {
		return move(p) && click(left);
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
				Delay.sleep(0, 350);
				if (predicate.apply(handler.getLocation())) {
					handler.click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
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
		if (!loc.equals(p1)) {
			if (move(p1)) {
				handler.press(p1.x, p1.y, button);
				if (move(p2)) {
					handler.release(p2.x, p2.y, button);
					return true;
				}
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

	public boolean move(final Targetable target, final Predicate<Point> predicate) {
		final MouseHandler handler = getMouseHandler();
		if (handler == null) {
			return false;
		}

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, predicate) {
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
