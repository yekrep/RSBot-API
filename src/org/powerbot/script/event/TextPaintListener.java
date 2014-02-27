package org.powerbot.script.event;

import java.awt.Graphics;
import java.util.EventListener;

/**
 */
public interface TextPaintListener extends EventListener {
	public int draw(int idx, Graphics render);
}
