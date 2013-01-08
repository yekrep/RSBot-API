package org.powerbot.core.event.impl;

import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.core.event.listeners.TextPaintListener;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.util.StringUtil;

public class TMousePosition implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Point point = Mouse.getLocation();
		StringUtil.drawLine(render, idx++, new StringBuilder("Mouse position: ").append(point.x).append(", ").append(point.y).toString());
		return idx;
	}
}
