package org.powerbot.script.lang;

import java.awt.Graphics;

public interface Drawable {
	public void draw(Graphics render);

	public void draw(Graphics render, int alpha);
}
