package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.xenon.Game;
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
