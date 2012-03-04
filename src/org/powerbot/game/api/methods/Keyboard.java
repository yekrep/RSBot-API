package org.powerbot.game.api.methods;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.powerbot.game.bot.Bot;

public class Keyboard {
	private static void pressKey(final char ch, final int delay, final int mask) {
		getKeyboard().keyPressed(
				new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, mask, ch, getKeyChar(ch), getLocation(ch))
				);
		if ((ch < KeyEvent.VK_LEFT || ch > KeyEvent.VK_DOWN) && (ch < KeyEvent.VK_SHIFT || ch > KeyEvent.VK_CAPS_LOCK)) {
			getKeyboard().keyTyped(
					new KeyEvent(getTarget(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + delay, mask, ch, getKeyChar(ch), getLocation(ch))
					);
		}
	}

	private static void releaseKey(final char ch, final int delay, final int mask) {
		getKeyboard().keyReleased(
				new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay, mask, ch, getKeyChar(ch), getLocation(ch))
				);
	}

	private static int getLocation(final char ch) {
		if (ch >= KeyEvent.VK_SHIFT && ch <= KeyEvent.VK_ALT) {
			return KeyEvent.KEY_LOCATION_LEFT;
		}
		return KeyEvent.KEY_LOCATION_STANDARD;
	}

	public static void sendKey(final char ch) {
		sendKey(ch, 0);
	}

	public static void sendKey(char ch, final int delay) {
		boolean shift = false;
		if (ch >= 'a' && ch <= 'z') {
			ch -= 32;
		} else if (ch >= 'A' && ch <= 'Z') {
			shift = true;
		}
		int wait = 0;
		if (shift) {
			pressKey((char) KeyEvent.VK_SHIFT, 0, InputEvent.SHIFT_DOWN_MASK);
			wait = Random.nextInt(100, 200);
		}
		pressKey(ch, wait, shift ? InputEvent.SHIFT_DOWN_MASK : 0);
		if (delay > 500) {
			pressKey(ch, 500 + wait, shift ? InputEvent.SHIFT_DOWN_MASK : 0);
			final int iterationWait = delay - 500;
			for (int i = 37; i < iterationWait; i += Random.nextInt(20, 40)) {
				pressKey(ch, 500 + i + wait, shift ? InputEvent.SHIFT_DOWN_MASK : 0);
			}
		}
		final int releasedDelay = delay + Random.nextInt(-30, 30);
		releaseKey(ch, releasedDelay + wait, shift ? InputEvent.SHIFT_DOWN_MASK : 0);
		if (shift) {
			releaseKey((char) KeyEvent.VK_SHIFT, releasedDelay + wait + Random.nextInt(50, 120), InputEvent.SHIFT_DOWN_MASK);
		}
	}

	public static void sendText(final String text, final boolean pressEnter) {
		sendText(text, pressEnter, 100, 200);
	}

	public static void sendText(final String text, final boolean pressEnter, final int minDelay, final int maxDelay) {
		final char[] chars = text.toCharArray();
		for (final char element : chars) {
			final int wait = Random.nextInt(minDelay, maxDelay);
			sendKey(element, wait);
			if (wait > 0) {
				Time.sleep(wait);
			}
		}
		if (pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, Random.nextInt(minDelay, maxDelay));
		}
	}

	private static org.powerbot.game.client.input.Keyboard getKeyboard() {
		final Bot bot = Bot.resolve();
		if (bot.client == null || bot.client.getCanvas() == null) {
			throw new RuntimeException("client not ready for events");
		}
		final KeyListener[] listeners = bot.client.getCanvas().getKeyListeners();
		if (listeners.length != 1) {
			throw new RuntimeException("listener mismatch");
		}
		return (org.powerbot.game.client.input.Keyboard) listeners[0];
	}

	private static Component getTarget() {
		final Bot bot = Bot.resolve();
		if (bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0) {
			throw new RuntimeException("client not ready for events");
		}
		return bot.appletContainer.getComponent(0);
	}

	private static char getKeyChar(final char c) {
		if (c >= 36 && c <= 40) {
			return KeyEvent.VK_UNDEFINED;
		}
		return c;
	}
}
