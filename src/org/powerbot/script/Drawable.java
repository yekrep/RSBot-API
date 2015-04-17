package org.powerbot.script;

import java.awt.Graphics;

public interface Drawable {
	void draw(Graphics render);

	void draw(Graphics render, int alpha);
}
