package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.core.event.listeners.TextPaintListener;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.util.StringUtil;

public class TDestination implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++, new StringBuilder("Destination: ").append(Walking.getDestination().toString()).toString());
		return idx;
	}
}
