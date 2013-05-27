package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.Movement;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.util.StringUtil;

public class TDestination implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final Tile dest = Movement.getDestination();
		StringUtil.drawLine(render, idx++, "Destination: " + (dest != null ? dest.toString() : "null"));
		return idx;
	}
}
