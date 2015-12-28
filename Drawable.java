package org.powerbot.script;

import java.awt.Graphics;

/**
 * Drawable
 * An entity which is rendered at varying opacity in the viewport.
 */
public interface Drawable {
	void draw(Graphics render);

	void draw(Graphics render, int alpha);
}
