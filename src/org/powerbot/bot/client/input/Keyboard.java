package org.powerbot.bot.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.powerbot.bot.RawAWTEvent;
import org.powerbot.bot.SelectiveEventQueue;

public abstract class Keyboard extends Focus implements KeyListener {
	public abstract void _keyPressed(KeyEvent e);

	public abstract void _keyReleased(KeyEvent e);

	public abstract void _keyTyped(KeyEvent e);

	public void keyPressed(final KeyEvent e) {
		_keyPressed(e);
	}

	public void keyReleased(final KeyEvent e) {
		_keyReleased(e);
	}

	public void keyTyped(final KeyEvent e) {
		_keyTyped(e);
	}

	public final void sendEvent(final KeyEvent e) {
		if (e == null) {
			return;
		}

		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		eq.focus();
		eq.postEvent(new RawAWTEvent(e));
	}
}