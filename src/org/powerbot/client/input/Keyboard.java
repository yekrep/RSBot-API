package org.powerbot.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.powerbot.bot.BlockingEventQueue;
import org.powerbot.bot.RawAWTEvent;

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
		BlockingEventQueue.getEventQueue().postEvent(new RawAWTEvent(e));
	}
}