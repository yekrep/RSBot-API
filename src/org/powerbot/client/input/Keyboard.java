package org.powerbot.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.powerbot.gui.BotChrome;

public abstract class Keyboard extends Focus implements KeyListener {
	private BotChrome chrome;

	public Keyboard() {
		super();
		chrome = BotChrome.getInstance();
	}

	public abstract void _keyPressed(KeyEvent e);

	public abstract void _keyReleased(KeyEvent e);

	public abstract void _keyTyped(KeyEvent e);

	public void keyPressed(final KeyEvent e) {
		if (chrome.isBlocking()) {
			return;
		}
		_keyPressed(e);
	}

	public void keyReleased(final KeyEvent e) {
		if (chrome.isBlocking()) {
			return;
		}
		_keyReleased(e);
	}

	public void keyTyped(final KeyEvent e) {
		if (chrome.isBlocking()) {
			return;
		}
		_keyTyped(e);
	}

	public final void sendEvent(final KeyEvent e) {
		if (e == null) {
			return;
		}

		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			_keyPressed(e);
			break;
		case KeyEvent.KEY_TYPED:
			_keyTyped(e);
			break;
		case KeyEvent.KEY_RELEASED:
			_keyReleased(e);
			break;
		default:
			throw new InternalError();
		}
	}
}