package org.powerbot.client.event.debug;

import java.awt.Graphics;

import org.powerbot.client.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.util.StringUtil;

public class TCamera implements TextPaintListener {
	@Override
	public int draw(int idx, final Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		StringUtil.drawLine(render, idx++,
				"Camera: " + "X: " + ctx.camera.getX() + " Y: " + ctx.camera.getY() + " Z: " + ctx.camera.getZ() + " Yaw: " + ctx.camera.getYaw() + " Pitch: " + ctx.camera.getPitch()
		);
		return idx;
	}
}
