package org.powerbot.script.internal.input;

import java.applet.Applet;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Queue;

import org.powerbot.game.client.Client;
import org.powerbot.game.client.input.Keyboard;

public class InputHandler {
	private final Applet applet;
	private final Client client;

	public InputHandler(final Applet applet, final Client client) {
		this.applet = applet;
		this.client = client;
	}

	public void send(final String str) {
		send(getKeyEvents(str));
	}

	public void send(final Queue<KeyEvent> queue) {
		final Keyboard keyboard = client.getKeyboard();
		if (keyboard == null) return;
		while (!queue.isEmpty()) {
			final KeyEvent e = queue.poll();
			keyboard.sendEvent(e);
		}
	}

	public void send(final KeyEvent e) {
		final Keyboard keyboard = client.getKeyboard();
		if (keyboard != null) keyboard.sendEvent(e);
	}

	private Queue<KeyEvent> getKeyEvents(final String sequence) {
		final Queue<String> list = new LinkedList<>();
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
		final Queue<KeyEvent> queue = new LinkedList<>();

		while (!sequence.isEmpty()) {
			String s = sequence.poll();

			if (s.length() == 1) { // simple letter
				final char c = s.charAt(0);
				final int vk = KeyEvent.getExtendedKeyCodeForChar((int) c);
				if (c == '\r') continue;
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
								final int vkx = KeyEvent.getExtendedKeyCodeForChar((int) cx);
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
				s = p[0];
				for (final Field f : KeyEvent.class.getFields()) {
					if (f.getName().startsWith(prefix) && Modifier.isPublic(f.getModifiers()) &&
							Modifier.isStatic(f.getModifiers()) && f.getType().equals(int.class) &&
							f.getName().equalsIgnoreCase(prefix + s)) {
						int vk = KeyEvent.VK_UNDEFINED;
						try {
							vk = f.getInt(null);
						} catch (final Exception ignored) {
						}
						if (vk == KeyEvent.VK_UNDEFINED) {
							throw new IllegalArgumentException("invalid keyString");
						}
						final boolean[] states = {false, false};
						if (p.length > 1 && p[1] != null && !p[1].isEmpty()) {
							switch (p[1].trim().toLowerCase()) {
							case "down":
							case "press":
							case "pressed":
								states[0] = true;
								break;
							case "up":
							case "release":
							case "released":
								states[1] = true;
								break;
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
		return new KeyEvent(getSource(), id, System.currentTimeMillis(), 0, vk, c);
	}

	private Component getSource() {
		return applet.getComponentCount() > 0 ? applet.getComponent(0) : null;
	}
}