package org.powerbot.script.internal.randoms;

import java.awt.Graphics;

import org.powerbot.script.PollingScript;
import org.powerbot.script.event.PaintListener;

public abstract class AntiRandom extends PollingScript implements PaintListener {

	public abstract boolean valid();

	@Override
	public void onRepaint(final Graphics g) {
		//TODO random paint
	}
}
