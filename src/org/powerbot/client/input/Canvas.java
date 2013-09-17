package org.powerbot.client.input;

import org.powerbot.gui.BotChrome;

public class Canvas extends java.awt.Canvas {
	@Override
	public void setVisible(boolean visible) {
		BotChrome.getInstance().target(this);
		super.setVisible(visible);
	}
}