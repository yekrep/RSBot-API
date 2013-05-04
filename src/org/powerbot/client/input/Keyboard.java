package org.powerbot.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
		if (e == null) return;
		try {
			switch (e.getID()) {
			case KeyEvent.KEY_PRESSED:
				_keyPressed(e);
				break;
			case KeyEvent.KEY_RELEASED:
				_keyReleased(e);
				break;
			case KeyEvent.KEY_TYPED:
				_keyTyped(e);
				break;
			default:
				throw new InternalError(e.toString());
			}
		} catch (final Exception ignored) {
		}
	}
}
