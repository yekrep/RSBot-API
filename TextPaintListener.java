package org.powerbot.script;

import java.awt.Graphics;
import java.util.EventListener;

/**
 * TextPaintListener
 * A listener for a text event of a certain index within an array of string being painted on the game canvas.
 */
public interface TextPaintListener extends EventListener {
	int draw(int idx, Graphics render);
}
