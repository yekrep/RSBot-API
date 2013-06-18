package org.powerbot.script.methods;

import org.powerbot.script.internal.InputHandler;

public class Keyboard extends MethodProvider {
	public Keyboard(MethodContext factory) {
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

	public boolean isReady() {
		final InputHandler inputHandler = getInputHandler();
		return inputHandler != null && inputHandler.getSource() != null;
	}

	private InputHandler getInputHandler() {
		return ctx.getBot().getInputHandler();
	}
}
