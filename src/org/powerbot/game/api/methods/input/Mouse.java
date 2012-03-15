package org.powerbot.game.api.methods.input;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.game.api.util.Random;
import org.powerbot.game.bot.Bot;

public class Mouse {
	private static final Map<ThreadGroup, Integer> dragLengths = new HashMap<ThreadGroup, Integer>();
	private static final Map<ThreadGroup, Integer> sides = new HashMap<ThreadGroup, Integer>();

	public static int getX() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? -1 : mouse.getX();
	}

	public static int getY() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? -1 : mouse.getY();
	}

	public static Point getLocation() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? new Point(-1, -1) : new Point(mouse.getX(), mouse.getY());
	}

	public static int getPressX() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? -1 : mouse.getPressX();
	}

	public static int getPressY() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? -1 : mouse.getPressY();
	}

	public static long getPressTime() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse == null ? -1L : mouse.getPressTime();
	}

	public static boolean isPresent() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse != null && mouse.isPresent();
	}

	public static boolean isPressed() {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		return mouse != null && mouse.isPressed();
	}

	public static void hopMouse(final int x, final int y) {
		hopMouse(x, y, 0, 0);
	}

	public static void hopMouse(int x, int y, final int randomX, final int randomY) {
		if (isOnCanvas(x, y)) {
			moveMouse(x + Random.nextGaussian(-randomX, randomX, randomX), Random.nextGaussian(-randomY, randomY, randomY));
		}
	}

	private static void pressMouse(final int x, final int y, final boolean left) {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		final Component target = getTarget();
		if (mouse == null || target == null ||
				mouse.isPressed() || !mouse.isPresent()) {
			return;
		}
		mouse.sendEvent(
				new MouseEvent(target, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3)
		);
	}

	private static void moveMouse(final int x, final int y) {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		final Component target = getTarget();
		if (mouse == null || target == null) {
			return;
		}
		if (mouse.isPressed()) {
			mouse.sendEvent(
					new MouseEvent(target, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, x, y, 0, false)
			);
			if ((getDragLength() & 0xFF) != 0xFF) {
				putDragLength(getDragLength() + 1);
			}
		}

		if (!mouse.isPresent()) {
			if (isOnCanvas(x, y)) {
				mouse.sendEvent(
						new MouseEvent(target, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, x, y, 0, false)
				);
			}
		} else if (!isOnCanvas(x, y)) {
			mouse.sendEvent(
					new MouseEvent(target, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, x, y, 0, false)
			);
			final Bot bot = Bot.resolve();
			final Canvas canvas;
			if (bot != null && (canvas = bot.getCanvas()) != null) {
				final int w = canvas.getWidth(), h = canvas.getHeight(), d = 50;
				if (x < d) {
					if (y < d) {
						putSide(4);
					} else if (y > h + d) {
						putSide(2);
					} else {
						putSide(1);
					}
				} else if (x > w) {
					putSide(3);
				} else {
					putSide(Random.nextInt(1, 5));
				}
			}
		} else if (!mouse.isPressed()) {
			mouse.sendEvent(
					new MouseEvent(target, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false)
			);
		}
	}

	private static void releaseMouse(final int x, final int y, final boolean left) {
		final org.powerbot.game.client.input.Mouse mouse = getMouse();
		final Component target = getTarget();
		if (mouse == null || target == null ||
				!mouse.isPressed()) {
			return;
		}
		mouse.sendEvent(
				new MouseEvent(target, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3)
		);
		if ((getDragLength() & 0xFF) <= 3) {
			mouse.sendEvent(
					new MouseEvent(target, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3)
			);
		}
		putDragLength(0);
	}

	/**
	 * Returns the mouse associated with this thread-group to relay events to.
	 *
	 * @return The <code>org.powerbot.game.client.input.Mouse</code> to relay events to.
	 */
	private static org.powerbot.game.client.input.Mouse getMouse() {
		final Bot bot = Bot.resolve();
		if (bot.client == null) {
			return null;
		}
		return bot.client.getMouse();
	}

	/**
	 * The component associated with this thread-group to dispatch events onto.
	 *
	 * @return The <code>Component</code> to dispatch events to.
	 */
	private static Component getTarget() {
		final Bot bot = Bot.resolve();
		if (bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0) {
			return null;
		}
		return bot.appletContainer.getComponent(0);
	}

	private static int getDragLength() {
		Integer integer = dragLengths.get(Thread.currentThread().getThreadGroup());
		if (integer == null) {
			integer = 0;
			dragLengths.put(Thread.currentThread().getThreadGroup(), integer);
		}
		return integer;
	}

	private static void putDragLength(final int length) {
		dragLengths.put(Thread.currentThread().getThreadGroup(), length);
	}

	private static int getSide() {
		Integer integer = sides.get(Thread.currentThread().getThreadGroup());
		if (integer == null) {
			integer = 0;
			sides.put(Thread.currentThread().getThreadGroup(), integer);
		}
		return integer;
	}

	private static void putSide(final int length) {
		sides.put(Thread.currentThread().getThreadGroup(), length);
	}

	public static boolean isOnCanvas(final int x, final int y) {
		final Bot bot = Bot.resolve();
		final Canvas canvas;
		return !(bot == null || (canvas = bot.getCanvas()) == null) && x > 0 && x < canvas.getWidth() && y > 0 && y < canvas.getHeight();
	}
}
