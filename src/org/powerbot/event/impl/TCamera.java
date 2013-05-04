package org.powerbot.event.impl;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.script.xenon.Camera;
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
