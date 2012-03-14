package org.powerbot.game.api.wrappers;

import java.awt.Graphics;

public interface Renderable {
	/**
	 * Draws the entity in detail.
	 *
	 * @param render The render to paint onto.
	 */
	public void draw(final Graphics render);
}
