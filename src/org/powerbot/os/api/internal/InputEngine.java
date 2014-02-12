package org.powerbot.os.api.internal;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InputEngine {//TODO: handle component focus!!!  Track click count [same mouse button].
	private final Component component;
	private final AtomicBoolean mousePresent;
	private final AtomicBoolean[] mousePressed;
	private final AtomicInteger mouseX, mouseY;

	public InputEngine(final Component component) {
		this.component = component;
		mousePresent = new AtomicBoolean(false);
		mousePressed = new AtomicBoolean[]{null, new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)};
		mouseX = new AtomicInteger(0);
		mouseY = new AtomicInteger(0);
	}

	public void press(final int button) {
		if (button < 1 || button >= mousePressed.length) {
			return;
		}
		if (!(mousePresent.get() || isDragging()) || mousePressed[button].get()) {
			return;
		}
		final int m = getMask(button, true);
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), m, mouseX.get(), mouseY.get(), 1, false, button);
		mousePressed[button].set(true);
		System.out.println(e.paramString());
		//TODO: dispatch
	}

	public void release(final int button) {
		if (button < 1 || button >= mousePressed.length) {
			return;
		}
		if (!mousePressed[button].get()) {
			return;
		}
		final int m = getMask(button, false);
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), m, mouseX.get(), mouseY.get(), 1, false, button);
		mousePressed[button].set(false);
		System.out.println(e.paramString());
		//TODO: dispatch
	}

	public void move(final int x, final int y) {
		final boolean in = x >= 0 && y >= 0 && x < component.getWidth() && y < component.getHeight();
		final int m = getMask();
		if (in) {
			if (mousePresent.get()) {
				if (!isDragging()) {
					final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), m, x, y, 0, false);
					mouseX.set(x);
					mouseY.set(y);
					System.out.println(e.paramString());
					//TODO: dispatch
				}
				postDrag(x, y);
			} else {
				final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), m, x, y, 0, false);
				mousePresent.set(true);
				mouseX.set(x);
				mouseY.set(y);
				System.out.println(e.paramString());
				//TODO: dispatch
				postDrag(x, y);
			}
		} else if (mousePresent.get()) {
			final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), m, x, y, 0, false);
			mousePresent.set(false);
			mouseX.set(x);
			mouseY.set(y);
			System.out.println(e.paramString());
			//TODO: dispatch
			postDrag(x, y);
		} else {
			postDrag(x, y);
		}
	}

	private int getMask() {
		return getMask(MouseEvent.NOBUTTON, false);
	}

	private int getMask(final int button, final boolean press) {
		final int[] buttons = {MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3};
		final int[] down = {InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON2_DOWN_MASK, InputEvent.BUTTON3_DOWN_MASK};
		final int[] up = {InputEvent.BUTTON1_MASK, InputEvent.BUTTON2_MASK, InputEvent.BUTTON3_MASK};
		final int[] extra = {0, InputEvent.ALT_MASK, InputEvent.META_MASK};
		int m = 0;
		for (int i = 0; i < buttons.length; i++) {
			final int b = buttons[i];
			if (mousePressed[b].get() || button == b) {
				if (button != b || press) {
					m |= down[i];
				} else {
					m |= up[i];
				}
				m |= extra[i];
			}
		}
		//TODO: account for keyboard alt, shift, ctrl
		//TODO: InputEvent.ALT_DOWN_MASK;
		//TODO: InputEvent.CTRL_DOWN_MASK;
		//TODO: InputEvent.SHIFT_DOWN_MASK;
		return m;
	}

	private boolean isDragging() {
		final int[] arr = {MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3};
		for (final int i : arr) {
			if (mousePressed[i].get()) {
				return true;
			}
		}
		return false;
	}

	private void postDrag(final int x, final int y) {
		if (!isDragging()) {
			return;
		}
		final int m = getMask();
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), m, x, y, 0, false);
		mouseX.set(x);
		mouseY.set(y);
		System.out.println(e.paramString());
		//TODO: dispatch
	}
}
