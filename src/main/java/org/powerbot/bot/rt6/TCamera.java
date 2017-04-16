package org.powerbot.bot.rt6;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TCamera implements TextPaintListener {
	private final ClientContext ctx;

	public TCamera(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, "Yaw: " + ctx.camera.yaw());
		drawLine(render, idx++, "Pitch: " + ctx.camera.pitch());
		return idx;
	}
}
