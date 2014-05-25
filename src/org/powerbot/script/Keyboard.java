package org.powerbot.script;

import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.SelectiveEventQueue;

public class Keyboard<C extends ClientContext> extends ClientAccessor<C> {
	private final SelectiveEventQueue queue;

	public Keyboard(final C ctx) {
		super(ctx);//TODO: document
		queue = SelectiveEventQueue.getInstance();
	}

	public boolean send(final String str) {
		return send(str, false);
	}

	public boolean send(final String str, final boolean newLine) {
		final InputSimulator engine = queue.getEngine();
		if (engine != null) {
			engine.send(str + (newLine ? '\n' : ""));
			return true;
		}
		return false;
	}

	public boolean sendln(final String str) {
		return send(str, true);
	}
}
