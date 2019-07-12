package org.powerbot.bot.rt6;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;

import java.awt.*;

import static org.powerbot.bot.DebugHelper.*;

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
