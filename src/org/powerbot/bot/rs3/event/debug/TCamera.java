package org.powerbot.bot.rs3.event.debug;

import java.awt.Graphics;

import org.powerbot.script.event.TextPaintListener;
import org.powerbot.script.rs3.tools.MethodContext;

import static org.powerbot.bot.rs3.event.debug.DebugHelper.drawLine;

public class TCamera implements TextPaintListener {
	private final MethodContext ctx;

	public TCamera(final MethodContext ctx) {
		this.ctx = ctx;
	}

	public int draw(int idx, final Graphics render) {
		drawLine(render, idx++, String.format("X: %d, Y: %d, Z: %d", ctx.camera.getX(), ctx.camera.getY(), ctx.camera.getZ()));
		drawLine(render, idx++, "Yaw: " + ctx.camera.getYaw());
		drawLine(render, idx++, "Pitch: " + ctx.camera.getPitch());
		return idx;
	}
}
