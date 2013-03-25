package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Game;
import org.powerbot.script.event.TextPaintListener;
import org.powerbot.util.StringUtil;

/**
 * @author Timer
 */
public class TPlane implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++, "Plane: " + Game.getPlane());
		return idx;
	}
}
