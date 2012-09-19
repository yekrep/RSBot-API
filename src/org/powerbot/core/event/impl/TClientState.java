package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Game;
import org.powerbot.core.event.listeners.TextPaintListener;
import org.powerbot.util.StringUtil;

/**
 * @author Timer
 */
public class TClientState implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++, "Client state: " + Game.getClientState());
		return idx;
	}
}
