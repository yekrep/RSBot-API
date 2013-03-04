package org.powerbot.script.internal.randoms;

import java.awt.Graphics;
import java.util.logging.Logger;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.job.LoopTask;

public abstract class AntiRandom extends LoopTask implements PaintListener {
	protected final Logger log = Logger.getLogger(getClass().getName());

	public abstract boolean valid();

	public void onStart() {
	}

	public void onStop() {
	}

	@Override
	public void onRepaint(final Graphics g) {
		//TODO random paint
	}
}
