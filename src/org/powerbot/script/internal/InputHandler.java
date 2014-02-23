package org.powerbot.script.internal;

import java.applet.Applet;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.powerbot.bot.client.Client;
import org.powerbot.bot.client.input.Keyboard;
import org.powerbot.script.util.Random;
import org.powerbot.util.math.HardwareSimulator;

public class InputHandler {
	private final Applet applet;
	private final Client client;
	private final Method getVK;
	private final Field when;
	private final Map<String, Integer> keyMap = new HashMap<String, Integer>(256);

	public InputHandler(final Applet applet, final Client client) {
		this.applet = applet;
		this.client = client;

		Method getVK = null;
		try {
			getVK = KeyEvent.class.getDeclaredMethod("getExtendedKeyCodeForChar", int.class);
		} catch (final NoSuchMethodException ignored) {
		}
		this.getVK = getVK;

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

		Field when = null;
		try {
			when = InputEvent.class.getDeclaredField("when");
		} catch (final NoSuchFieldException ignored) {
		}
		this.when = when;
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
		}
		return KeyEvent.VK_UNDEFINED;
	}

	public void send(final String str) {
		send(str, false);
	}

	public void send(final String str, final boolean async) {
		send(getKeyEvents(str), async);
	}

	public void send(final Queue<KeyEvent> queue, final boolean async) {
		final Keyboard keyboard = client.getKeyboard();
		if (keyboard == null) {
			return;
		}

		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!queue.isEmpty()) {
					keyboard.sendEvent(retimeKeyEvent(queue.poll()));
					final KeyEvent keyEvent = queue.peek();
					if (keyEvent != null && keyEvent.getID() != KeyEvent.KEY_TYPED) {
						try {
							Thread.sleep((long) (HardwareSimulator.getDelayFactor() * (1 + Random.nextDouble() / 2)));
						} catch (final InterruptedException ignored) {
						}
					}
				}
			}
		});
		t.start();

		if (!async) {
			try {
				t.join();
			} catch (final InterruptedException ignored) {
			}
		}
	}

	public void send(final KeyEvent e) {
		final Keyboard keyboard = client.getKeyboard();
		if (keyboard != null) {
			keyboard.sendEvent(e);
		}
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
					throw new IllegalArgumentException("invalid keyChar");
				} else {
					if (Character.isUpperCase(c)) {
						queue.add(constructKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT));
						pushUpperAlpha(queue, vk, c);
						while (!sequence.isEmpty()) {
							final String sx = sequence.peek();
							final char cx;
							if (sx.length() == 1 && Character.isUpperCase(cx = sx.charAt(0))) {
								final int vkx = getExtendedKeyCodeForChar(cx);
								if (vkx != KeyEvent.VK_UNDEFINED) {
									sequence.poll();
									pushUpperAlpha(queue, vkx, cx);
									continue;
								}
							}
							break;
						}
						queue.add(constructKeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.VK_SHIFT));
					} else {
						pushAlpha(queue, vk, c);
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
					queue.add(constructKeyEvent(KeyEvent.KEY_PRESSED, vk));
				}
				if (states[1]) {
					queue.add(constructKeyEvent(KeyEvent.KEY_RELEASED, vk));
				}
			}
		}

		return queue;
	}

	private void pushUpperAlpha(final Queue<KeyEvent> queue, final int vk, final char c) {
		final char l = String.valueOf(c).toLowerCase().charAt(0);
		pushAlpha(queue, vk, c, l);
	}

	private void pushAlpha(final Queue<KeyEvent> queue, final int vk, final char c) {
		pushAlpha(queue, vk, c, c);
	}

	private void pushAlpha(final Queue<KeyEvent> queue, final int vk, final char c0, final char c1) {
		queue.add(constructKeyEvent(KeyEvent.KEY_PRESSED, vk, c1));
		queue.add(constructKeyEvent(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, c0));
		queue.add(constructKeyEvent(KeyEvent.KEY_RELEASED, vk, c1));
	}

	public KeyEvent constructKeyEvent(final int id, final int vk) {
		return constructKeyEvent(id, vk, KeyEvent.CHAR_UNDEFINED);
	}

	public KeyEvent constructKeyEvent(final int id, final int vk, final char c) {
		int loc = KeyEvent.KEY_LOCATION_STANDARD;
		if (vk >= KeyEvent.VK_SHIFT && vk <= KeyEvent.VK_ALT) {
			loc = KeyEvent.KEY_LOCATION_LEFT; // because right variations don't exist on all keyboards
		}
		if (id == KeyEvent.KEY_TYPED) {
			loc = KeyEvent.KEY_LOCATION_UNKNOWN;
		}
		return new KeyEvent(getSource(), id, System.currentTimeMillis(), 0, vk, c, loc);
	}

	public KeyEvent retimeKeyEvent(final KeyEvent e) {
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

	public Component getSource() {
		return applet.getComponentCount() > 0 ? applet.getComponent(0) : null;
	}
}