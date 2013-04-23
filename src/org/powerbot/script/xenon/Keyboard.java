package org.powerbot.script.xenon;

import java.awt.event.KeyEvent;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.input.InputHandler;

public class Keyboard {
	public static void type(final String str) {
		type(str, false);
	}

	public static void type(String str, final boolean pressEnter) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		if (pressEnter) str += '\n';
		inputHandler.send(str);
	}

	public static void pressKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk));
	}

	public static void pressKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk, c));
	}

	public static void releaseKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk));
	}

	public static void releaseKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk, c));
	}
}
