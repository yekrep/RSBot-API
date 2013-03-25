package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.Walking;
import org.powerbot.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TDestination implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++, "Destination: " + Walking.getDestination().toString());
		return idx;
	}
}
