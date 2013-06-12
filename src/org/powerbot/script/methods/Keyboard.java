package org.powerbot.script.methods;

import java.awt.event.KeyEvent;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.InputHandler;

public class Keyboard extends ClientLink {
	public Keyboard(ClientFactory factory) {
		super(factory);
	}

	public boolean send(final String str) {
		return send(str, false);
	}

	public boolean send(String str, final boolean newLine) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) {
			return false;
		}
		if (newLine) {
			str += '\n';
		}
		inputHandler.send(str);
		return true;
	}

	public boolean sendln(final String str) {
		return send(str, true);
	}

	public boolean pressKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk));
		return true;
	}

	public boolean pressKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk, c));
		return true;
	}

	public boolean releaseKey(final int vk) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk));
		return true;
	}

	public boolean releaseKey(final int vk, final char c) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk, c));
		return true;
	}

	public boolean isReady() {
		final InputHandler inputHandler = Bot.inputHandler();
		return inputHandler != null && inputHandler.getSource() != null;
	}
}
