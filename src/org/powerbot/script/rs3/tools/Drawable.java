package org.powerbot.script.rs3.tools;

import java.awt.Graphics;

public interface Drawable {
	public void draw(Graphics render);

	public void draw(Graphics render, int alpha);
}
