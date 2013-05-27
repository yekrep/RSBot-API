package org.powerbot.script.methods;

import java.awt.event.KeyEvent;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.InputHandler;

public class Keyboard {//TODO patch up return trues.

	public static boolean send(final String str) {
		return send(str, false);
	}

	public static boolean send(String str, final boolean newLine) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return false;
		if (newLine) str += '\n';
		inputHandler.send(str);
		return true;
	}

	public static boolean sendln(final String str) {
		return send(str, true);
	}

	public static boolean pressKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return false;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk));
		return true;
	}

	public static boolean pressKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return false;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk, c));
		return true;
	}

	public static boolean releaseKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return false;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk));
		return true;
	}

	public static boolean releaseKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return false;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk, c));
		return true;
	}

	public static boolean isReady() {
		final InputHandler inputHandler = Bot.inputHandler();
		return inputHandler != null && inputHandler.getSource() != null;
	}
}
