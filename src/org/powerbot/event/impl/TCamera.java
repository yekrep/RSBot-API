package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.bot.Bot;
import org.powerbot.event.TextPaintListener;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.util.StringUtil;

public class TCamera implements TextPaintListener {
	@Override
	public int draw(int idx, final Graphics render) {
		ClientFactory ctx = Bot.getInstance().clientFactory;
		StringUtil.drawLine(render, idx++,
				"Camera: " + "X: " + ctx.camera.getX() + " Y: " + ctx.camera.getY() + " Z: " + ctx.camera.getZ() + " Yaw: " + ctx.camera.getYaw() + " Pitch: " + ctx.camera.getPitch()
		);
		return idx;
	}
}
