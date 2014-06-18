package org.powerbot.bot;

import java.awt.AWTEvent;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.script.Bot;
import org.powerbot.script.Condition;
import org.powerbot.script.Input;
import org.powerbot.script.Random;

public class InputSimulator extends Input {
	private final Bot bot;
	private final AtomicBoolean m;
	private final AtomicBoolean[] p;
	private final AtomicInteger mx, my, px, py, clicks;
	private final AtomicLong pw, mc;
	private final Point[] pp;
	public final Queue<AWTEvent> eq;

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

		Field x = null;
		try {
			x = KeyEvent.class.getDeclaredField("extendedKeyCode");
		} catch (final NoSuchFieldException ignored) {
		}
		extendedKeyCode = x;

		Field w = null;
		try {
			w = InputEvent.class.getDeclaredField("when");
		} catch (final NoSuchFieldException ignored) {
		}
		when = w;

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

	public InputSimulator(final Bot bot) {
		this.bot = bot;
		m = new AtomicBoolean(false);
		p = new AtomicBoolean[]{null, new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)};
		mx = new AtomicInteger(0);
		my = new AtomicInteger(0);
		px = new AtomicInteger(0);
		py = new AtomicInteger(0);
		pw = new AtomicLong(-1);
		final Object o = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		mc = new AtomicLong(o instanceof Integer ? (Integer) o : 500L);
		clicks = new AtomicInteger(0);
		pp = new Point[]{null, new Point(-1, -1), new Point(-1, -1), new Point(-1, -1)};
		eq = new ConcurrentLinkedQueue<AWTEvent>();

		final Component c = getComponent();
		final Point p;
		if (c != null && (p = c.getMousePosition()) != null && getComponent().isFocusOwner() && getComponent().isShowing()) {
			m.set(true);
			mx.set(p.x);
			my.set(p.y);
		}
	}

	public Component getComponent() {
		final Component[] c;
		return bot.applet == null || (c = bot.applet.getComponents()).length == 0 ? null : c[0];
	}

	private void postEvent(final AWTEvent e) {
		if (e instanceof InputEvent) {
			eq.offer(e);
		}
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
	}

	public void redirect(final AWTEvent e) {
		final Component c = getComponent();
		if (c == null) {
			return;
		}

		e.setSource(c);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
	}

	public void processEvent(final AWTEvent e) {
		if (e instanceof MouseEvent) {
			final MouseEvent m = (MouseEvent) e;
			mx.set(m.getX());
			my.set(m.getY());
		}
	}

	@Override
	public void focus() {
		final Component c;
		if ((c = getComponent()) != null && (!c.isFocusOwner() || !c.isShowing())) {
			postEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false, null));
			Condition.sleep(200);
		}
	}

	@Override
	public void defocus() {
		final Component c;
		if ((c = getComponent()) != null && c.isFocusOwner() && c.isShowing()) {
			postEvent(new FocusEvent(c, FocusEvent.FOCUS_LOST, false, null));
			Condition.sleep(200);
		}
	}

	@Override
	public Point getLocation() {
		return new Point(mx.get(), my.get());
	}

	@Override
	public Point getPressLocation() {
		return new Point(px.get(), py.get());
	}

	@Override
	public long getPressWhen() {
		return pw.get();
	}

	public boolean press(final int button) {
		final Component component = getComponent();
		if (component == null || button < 1 || button >= p.length) {
			return false;
		}
		if (!m.get() && !isDragging()) {
			defocus();
			return false;
		}
		if (!(m.get() || isDragging()) || p[button].get()) {
			return false;
		}
		final int m = getMouseMask(button, true);
		final int x = mx.get(), y = my.get();
		final long w = System.currentTimeMillis();
		if (clicks.get() == 0) {
			clicks.set(1);
		} else if (w - pw.get() <= mc.get()) {
			clicks.getAndIncrement();
		}
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, w, m, x, y, clicks.get(), false, button);
		p[button].set(true);
		pp[button].move(x, y);
		px.set(x);
		py.set(y);
		pw.set(w);
		focus();
		postEvent(e);
		return true;
	}

	public boolean release(final int button) {
		//TODO: defocus on out?
		final Component component = getComponent();
		if (component == null || button < 1 || button >= p.length) {
			return false;
		}
		if (!p[button].get()) {
			return false;
		}
		final int m = getMouseMask(button, false);
		final int x = mx.get(), y = my.get();
		final long w = System.currentTimeMillis();
		if (w - pw.get() > mc.get()) {
			clicks.set(0);
		}
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, w, m, x, y, clicks.get(), false, button);
		p[button].set(false);
		postEvent(e);
		final Point p = this.pp[button].getLocation();
		if (p.x == x && p.y == y) {
			final MouseEvent e2 = new MouseEvent(component, MouseEvent.MOUSE_CLICKED, w, m, x, y, clicks.get(), false, button);
			postEvent(e2);
		}
		return true;
	}

	@Override
	public boolean setLocation(final Point p) {
		final Component component = getComponent();
		if (component == null) {
			return false;
		}
		final int x = p.x, y = p.y;
		final boolean in = x >= 0 && y >= 0 && x < component.getWidth() && y < component.getHeight();
		final int m = getMouseMask();
		clicks.set(0);//TODO: inspect this operation
		if (in) {
			if (this.m.get()) {
				if (!isDragging()) {
					final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), m, x, y, clicks.get(), false);
					mx.set(x);
					my.set(y);
					postEvent(e);
				}
				postDrag(x, y);
			} else {
				final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), m, x, y, clicks.get(), false);
				this.m.set(true);
				mx.set(x);
				my.set(y);
				postEvent(e);
				postDrag(x, y);
			}
		} else if (this.m.get()) {
			final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), m, x, y, clicks.get(), false);
			this.m.set(false);
			mx.set(x);
			my.set(y);
			postEvent(e);
			postDrag(x, y);
		} else {
			postDrag(x, y);
		}
		return true;
	}

	public boolean scroll(final boolean down) {
		final Component component = getComponent();
		if (component == null) {
			return false;
		}
		//TODO: proper event
		final MouseEvent e = new MouseWheelEvent(component, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mx.get(), my.get(), 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, down ? 1 : -1);
		postEvent(e);
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
			if (p[b].get() || button == b) {
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
			if (p[i].get()) {
				return true;
			}
		}
		return false;
	}

	private void postDrag(final int x, final int y) {
		final Component component = getComponent();
		if (component == null || !isDragging()) {
			return;
		}
		clicks.set(0);//TODO: inspect this operation
		final int m = getMouseMask();
		final MouseEvent e = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), m, x, y, clicks.get(), false);
		mx.set(x);
		my.set(y);
		postEvent(e);
	}

	public static int getExtendedKeyCodeForChar(final char c) {
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
			case '@':
				return KeyEvent.VK_AT;
			case '.':
				return KeyEvent.VK_PERIOD;
			case ' ':
				return KeyEvent.VK_SPACE;
			case '!':
				return KeyEvent.VK_EXCLAMATION_MARK;
			case '/':
				return KeyEvent.VK_SLASH;
			case '\\':
				return KeyEvent.VK_BACK_SLASH;
			case '`':
				return KeyEvent.VK_BACK_QUOTE;
			case '\'':
				return KeyEvent.VK_QUOTE;
			case '"':
				return KeyEvent.VK_QUOTEDBL;
			case '-':
				return KeyEvent.VK_MINUS;
			case '+':
				return KeyEvent.VK_PLUS;
			case '$':
				return KeyEvent.VK_DOLLAR;
			case '_':
				return KeyEvent.VK_UNDERSCORE;
			case '(':
				return KeyEvent.VK_LEFT_PARENTHESIS;
			case ')':
				return KeyEvent.VK_RIGHT_PARENTHESIS;
			case '*':
				return KeyEvent.VK_ASTERISK;
			case '=':
				return KeyEvent.VK_EQUALS;
			case ':':
				return KeyEvent.VK_COLON;
			case ';':
				return KeyEvent.VK_SEMICOLON;
			case '<':
				return KeyEvent.VK_LESS;
			case '>':
				return KeyEvent.VK_GREATER;
			case '{':
				return KeyEvent.VK_BRACELEFT;
			case '}':
				return KeyEvent.VK_BRACERIGHT;
			}
		}
		return KeyEvent.VK_UNDEFINED;
	}

	public boolean send(final String str) {
		send(getKeyEvents(str));
		return true;
	}

	public void send(final Queue<KeyEvent> queue) {
		while (!queue.isEmpty()) {
			send(queue.poll());
			final KeyEvent keyEvent = queue.peek();
			if (keyEvent != null && keyEvent.getID() != KeyEvent.KEY_TYPED) {
				Condition.sleep((int) (Random.getDelay() * (1d + Random.nextDouble() / 2d)));
			}
		}
	}

	public void send(final KeyEvent e) {
		focus();
		postEvent(setKeyEventWhen(e));
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
		final Component component = getComponent();
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
						queue.add(newKeyEvent(component, KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
						addKeyTypeEvents(queue, component, vk, c, String.valueOf(c).toLowerCase().charAt(0));
						while (!sequence.isEmpty()) {
							final String sx = sequence.peek();
							final char cx;
							if (sx.length() == 1 && Character.isUpperCase(cx = sx.charAt(0))) {
								final int vkx = getExtendedKeyCodeForChar(cx);
								if (vkx != KeyEvent.VK_UNDEFINED) {
									sequence.poll();
									addKeyTypeEvents(queue, component, vkx, cx, String.valueOf(cx).toLowerCase().charAt(0));
									continue;
								}
							}
							break;
						}
						queue.add(newKeyEvent(component, KeyEvent.KEY_RELEASED, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
					} else {
						addKeyTypeEvents(queue, component, vk, c, c);
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
					queue.add(newKeyEvent(component, KeyEvent.KEY_PRESSED, vk, KeyEvent.CHAR_UNDEFINED));
				}
				if (states[1]) {
					queue.add(newKeyEvent(component, KeyEvent.KEY_RELEASED, vk, KeyEvent.CHAR_UNDEFINED));
				}
			}
		}

		return queue;
	}

	private static void addKeyTypeEvents(final Collection<KeyEvent> queue, final Component source, final int vk, final char c0, final char c1) {
		queue.add(newKeyEvent(source, KeyEvent.KEY_PRESSED, vk, c1));
		queue.add(newKeyEvent(source, KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, c0));
		queue.add(newKeyEvent(source, KeyEvent.KEY_RELEASED, vk, c1));
	}

	private static KeyEvent newKeyEvent(final Component source, final int id, final int vk, final char c) {
		int loc = KeyEvent.KEY_LOCATION_STANDARD;
		if (vk >= KeyEvent.VK_SHIFT && vk <= KeyEvent.VK_ALT) {
			loc = KeyEvent.KEY_LOCATION_LEFT; // because right variations don't exist on all keyboards
		}
		if (id == KeyEvent.KEY_TYPED) {
			loc = KeyEvent.KEY_LOCATION_UNKNOWN;
		}
		final KeyEvent k = new KeyEvent(source, id, System.currentTimeMillis(), 0, vk, c, loc);

		final long ex;
		if (extendedKeyCode != null && (ex = getExtendedKeyCodeForChar(c)) != KeyEvent.VK_UNDEFINED) {
			try {
				final boolean a = extendedKeyCode.isAccessible();
				extendedKeyCode.setAccessible(true);
				extendedKeyCode.setLong(k, ex);
				extendedKeyCode.setAccessible(a);
			} catch (final IllegalAccessException ignored) {
			}
		}

		return k;
	}

	private static KeyEvent setKeyEventWhen(final KeyEvent e) {
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
