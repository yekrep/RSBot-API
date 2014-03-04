package org.powerbot.script.rs3.tools;

import org.powerbot.bot.script.KeyboardSimulator;

public class Keyboard extends ClientAccessor {
	public Keyboard(final ClientContext factory) {
		super(factory);//TODO: document
	}

	public boolean send(final String str) {
		return send(str, false);
	}

	public boolean send(String str, final boolean newLine) {
		final KeyboardSimulator h = ctx.inputHandler.get();
		if (h == null) {
			return false;
		}
		if (newLine) {
			str += '\n';
		}
		h.send(str);
		return true;
	}

	public boolean sendln(final String str) {
		return send(str, true);
	}

	public boolean isReady() {
		final KeyboardSimulator h = ctx.inputHandler.get();
		return h != null && h.getSource() != null;
	}
}
