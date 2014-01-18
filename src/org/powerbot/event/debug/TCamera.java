package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.event.debug.DebugHelper.drawLine;

public class TCamera implements TextPaintListener {
	protected final MethodContext ctx;

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
