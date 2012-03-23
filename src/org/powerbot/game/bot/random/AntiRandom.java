package org.powerbot.game.bot.random;

import java.awt.Graphics;

import org.powerbot.concurrent.Task;
import org.powerbot.game.bot.event.listener.PaintListener;
import org.powerbot.lang.Activatable;

public abstract class AntiRandom extends Task implements Activatable, PaintListener {
	public void onRepaint(final Graphics render) {
	}
}
