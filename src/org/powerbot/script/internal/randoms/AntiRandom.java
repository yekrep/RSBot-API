package org.powerbot.script.internal.randoms;

import java.awt.Graphics;
import java.util.logging.Logger;

import org.powerbot.script.event.PaintListener;
import org.powerbot.script.task.LoopTask;

public abstract class AntiRandom extends LoopTask implements PaintListener {
	protected final Logger log = Logger.getLogger(getClass().getName());

	public abstract boolean valid();

	@Override
	public void onRepaint(final Graphics g) {
		//TODO random paint
	}
}
