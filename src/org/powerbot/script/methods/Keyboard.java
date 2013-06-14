package org.powerbot.script.methods;

import org.powerbot.script.internal.InputHandler;

import java.awt.event.KeyEvent;

public class Keyboard extends ClientLink {
	public Keyboard(ClientFactory factory) {
		super(factory);
	}

	public boolean send(final String str) {
		return send(str, false);
	}

	public boolean send(String str, final boolean newLine) {
		final InputHandler inputHandler = getInputHandler();
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
		final InputHandler inputHandler = getInputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk));
		return true;
	}

	public boolean pressKey(final int vk, final char c) {
		final InputHandler inputHandler = getInputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_PRESSED, vk, c));
		return true;
	}

	public boolean releaseKey(final int vk) {
		final InputHandler inputHandler = getInputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk));
		return true;
	}

	public boolean releaseKey(final int vk, final char c) {
		final InputHandler inputHandler = getInputHandler();
		if (inputHandler == null) {
			return false;
		}
		inputHandler.send(inputHandler.constructKeyEvent(KeyEvent.KEY_RELEASED, vk, c));
		return true;
	}

	public boolean isReady() {
		final InputHandler inputHandler = getInputHandler();
		return inputHandler != null && inputHandler.getSource() != null;
	}

	private InputHandler getInputHandler() {
		return ctx.bot.getInputHandler();
	}
}
