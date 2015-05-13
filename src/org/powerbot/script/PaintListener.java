package org.powerbot.script;

import java.awt.Graphics;
import java.util.EventListener;

/**
 * A listener that represents a class object that listens for paint events.
 */
public interface PaintListener extends EventListener {
	void repaint(Graphics render);
}
