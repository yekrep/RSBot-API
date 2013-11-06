package org.powerbot.client.input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.powerbot.gui.BotChrome;

public abstract class Focus implements FocusListener {
	private BotChrome chrome;

	public Focus() {
		super();
		chrome = BotChrome.getInstance();
	}

	public abstract void _focusGained(FocusEvent e);

	public abstract void _focusLost(FocusEvent e);

	public final void focusGained(final FocusEvent e) {
		if (chrome.isBlocking()) {
			return;
		}
		_focusGained(e);
	}

	public final void focusLost(final FocusEvent e) {
		if (chrome.isBlocking()) {
			return;
		}
		_focusLost(e);
	}
}
