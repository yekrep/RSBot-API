package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.script.event.TextPaintListener;
import org.powerbot.util.StringUtil;

public class TCamera implements TextPaintListener {
	@Override
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++,
				"Camera: " + "X: " + Camera.getX() + " Y: " + Camera.getY() + " Z: " + Camera.getZ() + " Yaw: " + Camera.getYaw() + " Pitch: " + Camera.getPitch()
		);
		return idx;
	}
}
