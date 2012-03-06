package org.powerbot.game.event.listener;

import java.awt.Graphics;
import java.util.EventListener;

public interface PaintListener extends EventListener {
	public void onRepaint(Graphics render);
}
