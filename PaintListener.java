package org.powerbot.script;

import java.awt.Graphics;
import java.util.EventListener;

/**
 * PaintListener
 * A listener that listens for canvas repainting events.
 */
public interface PaintListener extends EventListener {
	void repaint(Graphics render);
}
