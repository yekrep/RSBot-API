package org.powerbot.os.api.internal;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InputEngine {//TODO: handle component focus!!!
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
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), getMask(button), mouseX.get(), mouseY.get(), 1, false, button);
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
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), getMask(button), mouseX.get(), mouseY.get(), 1, false, button);
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
		return getMask(MouseEvent.NOBUTTON);
	}

	private int getMask(final int button) {
		int m = 0;
		m |= mousePressed[MouseEvent.BUTTON1].get() || button == MouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK : 0;
		m |= mousePressed[MouseEvent.BUTTON2].get() || button == MouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK | InputEvent.ALT_MASK : 0;
		m |= mousePressed[MouseEvent.BUTTON3].get() || button == MouseEvent.BUTTON3 ? InputEvent.BUTTON3_DOWN_MASK | InputEvent.META_MASK : 0;
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
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), getMask(), x, y, 0, false);
		mouseX.set(x);
		mouseY.set(y);
		System.out.println(e.paramString());
		//TODO: dispatch
	}
}
