package org.powerbot.bot;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.script.Random;

public class InputSimulator {//TODO: Track click count [same mouse button].
	private final AtomicBoolean focused, mousePresent;
	private final AtomicBoolean[] mousePressed;
	private final AtomicInteger mouseX, mouseY;
	private final AtomicInteger pressX, pressY;
	private final AtomicLong pressWhen;
	private final AtomicLong multiClickInterval;
	private final AtomicInteger clickCount;
	private final Point[] mousePressPoints;
	private Component component;

	private static final Method getVK;
	private static final Field when, extendedKeyCode;
	private static final Map<String, Integer> keyMap = new HashMap<String, Integer>(256);

	static {
		Method k = null;
		try {
			k = KeyEvent.class.getDeclaredMethod("getExtendedKeyCodeForChar", int.class);
		} catch (final NoSuchMethodException ignored) {
		}
		getVK = k;

		Field w = null, x = null;
		try {
			w = InputEvent.class.getDeclaredField("when");
			x = KeyEvent.class.getDeclaredField("extendedKeyCode");
		} catch (final NoSuchFieldException ignored) {
		}
		when = w;
		extendedKeyCode = x;

		final String prefix = "VK_";
		for (final Field f : KeyEvent.class.getFields()) {
			final int len = prefix.length();
			if (f.getName().startsWith(prefix) && Modifier.isPublic(f.getModifiers()) &&
					Modifier.isStatic(f.getModifiers()) && f.getType().equals(int.class) &&
					f.getName().startsWith(prefix)) {
				try {
					keyMap.put(f.getName().substring(len), f.getInt(null));
				} catch (final IllegalAccessException ignored) {
				}
			}
		}
	}

	public InputSimulator(final Component component) {
		this.component = component;
		focused = new AtomicBoolean(false);
		mousePresent = new AtomicBoolean(false);
		mousePressed = new AtomicBoolean[]{null, new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)};
		mouseX = new AtomicInteger(0);
		mouseY = new AtomicInteger(0);
		pressX = new AtomicInteger(0);
		pressY = new AtomicInteger(0);
		pressWhen = new AtomicLong(-1);
		final Object o = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		if (o instanceof Integer) {
			multiClickInterval = new AtomicLong((Integer) o);
		} else {
			multiClickInterval = new AtomicLong(500);
		}
		clickCount = new AtomicInteger(0);
		mousePressPoints = new Point[]{null, new Point(-1, -1), new Point(-1, -1), new Point(-1, -1)};

		final Point p = component.getMousePosition();
		if (p != null && component.isFocusOwner() && component.isShowing()) {
			mousePresent.set(true);
			mouseX.set(p.x);
			mouseY.set(p.y);
			focused.set(true);
		}
	}

	public Component getComponent() {
		return component;
	}

	public void focus() {
		if (focused.get() || component == null) {
			return;
		}
		if (!component.isFocusOwner() || !component.isShowing()) {
			SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_GAINED, false, null)));
			try {
				Thread.sleep(Random.nextInt(100, 200));
			} catch (final InterruptedException ignored) {
			}
		}
		focused.set(component.isFocusOwner() && component.isShowing());
	}

	public void defocus() {
		if (!focused.get() || component == null) {
			return;
		}
		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		eq.postEvent(new SelectiveEventQueue.RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_LOST, false, null)));
		eq.postEvent(new SelectiveEventQueue.RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_LOST, false, null)));
		focused.set(true);
		try {
			Thread.sleep(Random.nextInt(100, 200));
		} catch (final InterruptedException ignored) {
		}
	}

	public void destroy() {
		if (component == null) {
			return;
		}

		final Point p = component.getMousePosition();
		if (p != null && !mousePresent.get()) {
			move(p.x, p.y);
		} else if (p == null && focused.get() && mousePresent.get()) {
			move(-Random.nextInt(1, 11), -Random.nextInt(1, 11));
		}
		if (focused.get()) {
			defocus();
		}
		component = null;
	}

	public Point getLocation() {
		return new Point(mouseX.get(), mouseY.get());
	}

	public Point getPressLocation() {
		return new Point(pressX.get(), pressY.get());
	}

	public long getPressWhen() {
		return pressWhen.get();
	}

	public void press(final int button) {
		if (component == null || button < 1 || button >= mousePressed.length) {
			return;
		}
		if (!mousePresent.get() && !isDragging()) {
			defocus();
			return;
		}
		if (!(mousePresent.get() || isDragging()) || mousePressed[button].get()) {
			return;
		}
		final int m = getMouseMask(button, true);
		final int x = mouseX.get(), y = mouseY.get();
		final long w = System.currentTimeMillis();
		if (clickCount.get() == 0) {
			clickCount.set(1);
		} else if (w - pressWhen.get() <= multiClickInterval.get()) {
			clickCount.getAndIncrement();
		}
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, w, m, x, y, clickCount.get(), false, button);
		mousePressed[button].set(true);
		mousePressPoints[button].move(x, y);
		pressX.set(x);
		pressY.set(y);
		pressWhen.set(w);
		SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
		if (!focused.get()) {
			try {
				Thread.sleep(Random.nextInt(25, 50));
			} catch (final InterruptedException ignored) {
			}

			focus();
		}
	}

	public void release(final int button) {
		//TODO: defocus on out?
		if (component == null || button < 1 || button >= mousePressed.length) {
			return;
		}
		if (!mousePressed[button].get()) {
			return;
		}
		final int m = getMouseMask(button, false);
		final int x = mouseX.get(), y = mouseY.get();
		final long w = System.currentTimeMillis();
		if (w - pressWhen.get() > multiClickInterval.get()) {
			clickCount.set(0);
		}
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, w, m, x, y, clickCount.get(), false, button);
		mousePressed[button].set(false);
		SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
		final Point p = mousePressPoints[button].getLocation();
		if (p.x == x && p.y == y) {
			final MouseEvent e2 = new MouseEvent(component, MouseEvent.MOUSE_CLICKED, w, m, x, y, clickCount.get(), false, button);
			SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e2));
		}
	}

	public boolean move(final int x, final int y) {
		if (component == null) {
			return false;
		}
		final boolean in = x >= 0 && y >= 0 && x < component.getWidth() && y < component.getHeight();
		final int m = getMouseMask();
		clickCount.set(0);//TODO: inspect this operation
		if (in) {
			if (mousePresent.get()) {
				if (!isDragging()) {
					final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), m, x, y, clickCount.get(), false);
					mouseX.set(x);
					mouseY.set(y);
					SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
				}
				postDrag(x, y);
			} else {
				final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), m, x, y, clickCount.get(), false);
				mousePresent.set(true);
				mouseX.set(x);
				mouseY.set(y);
				SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
				postDrag(x, y);
			}
		} else if (mousePresent.get()) {
			final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), m, x, y, clickCount.get(), false);
			mousePresent.set(false);
			mouseX.set(x);
			mouseY.set(y);
			SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
			postDrag(x, y);
		} else {
			postDrag(x, y);
		}
		return true;
	}

	public boolean scroll(final boolean down) {
		if (component == null) {
			return false;
		}
		//TODO: proper event
		final MouseEvent e = new MouseWheelEvent(component, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mouseX.get(), mouseY.get(), 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, down ? 1 : -1);
		SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
		return true;
	}

	private int getMouseMask() {
		return getMouseMask(MouseEvent.NOBUTTON, false);
	}

	private int getMouseMask(final int button, final boolean press) {
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
		if (component == null || !isDragging()) {
			return;
		}
		clickCount.set(0);//TODO: inspect this operation
		final int m = getMouseMask();
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), m, x, y, clickCount.get(), false);
		mouseX.set(x);
		mouseY.set(y);
		SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(e));
	}

	public int getExtendedKeyCodeForChar(final char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || "\n\b\t".indexOf(c) != -1) {
			return (int) c;
		} else if (c >= 'a' && c <= 'z') {
			return (int) Character.toUpperCase(c);
		} else if (getVK != null) {
			try {
				return (Integer) getVK.invoke(null, (int) c);
			} catch (final InvocationTargetException ignored) {
			} catch (final IllegalAccessException ignored) {
			}
		} else {
			switch (c) {
			case '@': return KeyEvent.VK_AT;
			case '.': return KeyEvent.VK_PERIOD;
			case ' ': return KeyEvent.VK_SPACE;
			case '!': return KeyEvent.VK_EXCLAMATION_MARK;
			case '/': return KeyEvent.VK_SLASH;
			case '\\': return KeyEvent.VK_BACK_SLASH;
			case '`': return KeyEvent.VK_BACK_QUOTE;
			case '\'': return KeyEvent.VK_QUOTE;
			case '"': return KeyEvent.VK_QUOTEDBL;
			case '-': return KeyEvent.VK_MINUS;
			case '+': return KeyEvent.VK_PLUS;
			case '$': return KeyEvent.VK_DOLLAR;
			case '_': return KeyEvent.VK_UNDERSCORE;
			case '(': return KeyEvent.VK_LEFT_PARENTHESIS;
			case ')': return KeyEvent.VK_RIGHT_PARENTHESIS;
			case '*': return KeyEvent.VK_ASTERISK;
			case '=': return KeyEvent.VK_EQUALS;
			case ':': return KeyEvent.VK_COLON;
			case ';': return KeyEvent.VK_SEMICOLON;
			case '<': return KeyEvent.VK_LESS;
			case '>': return KeyEvent.VK_GREATER;
			case '{': return KeyEvent.VK_BRACELEFT;
			case '}': return KeyEvent.VK_BRACERIGHT;
			}
		}
		return KeyEvent.VK_UNDEFINED;
	}

	public void send(final String str) {
		send(getKeyEvents(str));
	}

	public void send(final Queue<KeyEvent> queue) {
		while (!queue.isEmpty()) {
			send(queue.poll());
			final KeyEvent keyEvent = queue.peek();
			if (keyEvent != null && keyEvent.getID() != KeyEvent.KEY_TYPED) {
				try {
					Thread.sleep((long) (Random.getDelay() * (1d + Random.nextDouble() / 2d)));
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}

	public void send(final KeyEvent e) {
		focus();
		SelectiveEventQueue.getInstance().postEvent(new SelectiveEventQueue.RawAWTEvent(retimeKeyEvent(e)));
	}

	private Queue<KeyEvent> getKeyEvents(final String sequence) {
		final Queue<String> list = new LinkedList<String>();
		boolean braced = false;
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < sequence.length(); i++) {
			final char c = sequence.charAt(i);
			switch (c) {
			case '{':
				braced = true;
				break;
			case '}':
				braced = false;
				if (buf.length() != 0) {
					list.add(buf.toString());
					buf = new StringBuilder();
				}
				break;
			default:
				if (braced) {
					buf.append(c);
				} else {
					list.add(String.valueOf(c));
				}
				break;
			}
		}

		return getKeyEvents(list);
	}

	private Queue<KeyEvent> getKeyEvents(final Queue<String> sequence) {
		final Queue<KeyEvent> queue = new LinkedList<KeyEvent>();

		while (!sequence.isEmpty()) {
			String s = sequence.poll();

			if (s.length() == 1) { // simple letter
				final char c = s.charAt(0);
				if (c == '\r') {
					continue;
				}
				final int vk = getExtendedKeyCodeForChar(c);
				if (vk == KeyEvent.VK_UNDEFINED) {
					throw new IllegalArgumentException("invalid keyChar (" + c + ")");
				} else {
					if (Character.isUpperCase(c)) {
						queue.add(constructKeyEvent(component, KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT));
						pushUpperAlpha(queue, component, vk, c);
						while (!sequence.isEmpty()) {
							final String sx = sequence.peek();
							final char cx;
							if (sx.length() == 1 && Character.isUpperCase(cx = sx.charAt(0))) {
								final int vkx = getExtendedKeyCodeForChar(cx);
								if (vkx != KeyEvent.VK_UNDEFINED) {
									sequence.poll();
									pushUpperAlpha(queue, component, vkx, cx);
									continue;
								}
							}
							break;
						}
						queue.add(constructKeyEvent(component, KeyEvent.KEY_RELEASED, KeyEvent.VK_SHIFT));
					} else {
						pushAlpha(queue, component, vk, c);
					}
				}
			} else { // more advanced key (F1, etc)
				final String prefix = "VK_";
				if (s.startsWith(prefix)) {
					s = s.substring(prefix.length());
				}
				final String[] p = s.split(" ", 2);
				final int vk = keyMap.containsKey(p[0]) ? keyMap.get(p[0]) : KeyEvent.VK_UNDEFINED;
				if (vk == KeyEvent.VK_UNDEFINED) {
					throw new IllegalArgumentException("invalid keyString");
				}
				final boolean[] states = {false, false};
				if (p.length > 1 && p[1] != null && !p[1].isEmpty()) {
					final String p1 = p[1].trim().toLowerCase();
					if (p1.equals("down") || p1.equals("press") || p1.equals("pressed")) {
						states[0] = true;
					} else if (p1.equals("up") || p1.equals("release") || p1.equals("released")) {
						states[1] = true;
					}
				} else {
					states[0] = true;
					states[1] = true;
				}
				if (states[0]) {
					queue.add(constructKeyEvent(component, KeyEvent.KEY_PRESSED, vk));
				}
				if (states[1]) {
					queue.add(constructKeyEvent(component, KeyEvent.KEY_RELEASED, vk));
				}
			}
		}

		return queue;
	}

	public static void pushUpperAlpha(final Queue<KeyEvent> queue, final Component source, final int vk, final char c) {
		final char l = String.valueOf(c).toLowerCase().charAt(0);
		pushAlpha(queue, source, vk, c, l);
	}

	public static void pushAlpha(final Queue<KeyEvent> queue, final Component source, final int vk, final char c) {
		pushAlpha(queue, source, vk, c, c);
	}

	private static void pushAlpha(final Queue<KeyEvent> queue, final Component source, final int vk, final char c0, final char c1) {
		queue.add(constructKeyEvent(source, KeyEvent.KEY_PRESSED, vk, c1));
		queue.add(constructKeyEvent(source, KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, c0));
		queue.add(constructKeyEvent(source, KeyEvent.KEY_RELEASED, vk, c1));
	}

	public static KeyEvent constructKeyEvent(final Component source, final int id, final int vk) {
		return constructKeyEvent(source, id, vk, KeyEvent.CHAR_UNDEFINED);
	}

	public static KeyEvent constructKeyEvent(final Component source, final int id, final int vk, final char c) {
		int loc = KeyEvent.KEY_LOCATION_STANDARD;
		if (vk >= KeyEvent.VK_SHIFT && vk <= KeyEvent.VK_ALT) {
			loc = KeyEvent.KEY_LOCATION_LEFT; // because right variations don't exist on all keyboards
		}
		if (id == KeyEvent.KEY_TYPED) {
			loc = KeyEvent.KEY_LOCATION_UNKNOWN;
		}
		final KeyEvent k = new KeyEvent(source, id, System.currentTimeMillis(), 0, vk, c, loc);

		if (extendedKeyCode != null) {
			try {
				final boolean a = extendedKeyCode.isAccessible();
				extendedKeyCode.setAccessible(true);
				extendedKeyCode.setLong(k, k.getKeyCode());
				extendedKeyCode.setAccessible(a);
			} catch (final IllegalAccessException ignored) {
			}
		}

		return k;
	}

	public static KeyEvent retimeKeyEvent(final KeyEvent e) {
		if (when != null) {
			try {
				final boolean a = when.isAccessible();
				when.setAccessible(true);
				when.setLong(e, System.currentTimeMillis());
				when.setAccessible(a);
			} catch (final IllegalAccessException ignored) {
			}
		}
		return e;
	}
}
