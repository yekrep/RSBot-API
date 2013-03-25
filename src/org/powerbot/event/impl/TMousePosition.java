package org.powerbot.event.impl;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TMousePosition implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Point point = Mouse.getLocation();
		StringUtil.drawLine(render, idx++, "Mouse position: " + point.x + ", " + point.y);
		return idx;
	}
}
