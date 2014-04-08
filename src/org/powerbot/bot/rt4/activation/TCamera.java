package org.powerbot.bot.rt4.activation;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

/**
 */
public class TCamera implements TextPaintListener {
	private final ClientContext ctx;

	public TCamera(final ClientContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, String.format("X: %d, Y: %d, Z: %d", ctx.camera.x(), ctx.camera.y(), ctx.camera.z()));
		drawLine(render, idx++, String.format("Yaw: %d, Pitch: %d", ctx.camera.yaw(), ctx.camera.pitch()));
		return idx;
	}
}
