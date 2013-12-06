package org.powerbot.script.methods;

public class Keyboard extends MethodProvider {
	public Keyboard(final MethodContext factory) {
		super(factory);//TODO: document
	}

	public boolean send(final String str) {
		return send(str, false);
	}

	public boolean send(String str, final boolean newLine) {
		if (ctx.inputHandler == null) {
			return false;
		}
		if (newLine) {
			str += '\n';
		}
		ctx.inputHandler.send(str);
		return true;
	}

	public boolean sendln(final String str) {
		return send(str, true);
	}

	public boolean isReady() {
		return ctx.inputHandler != null && ctx.inputHandler.getSource() != null;
	}
}
